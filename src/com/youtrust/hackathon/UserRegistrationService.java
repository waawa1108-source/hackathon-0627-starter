package com.youtrust.hackathon;

import java.util.List;
import java.util.logging.Logger;

/**
 * ユーザー登録のオーケストレーション。
 *
 * 設計の核：登録を「本人確認（プロバイダ依存）」と「後続処理（共通）」に分離する。
 *   1. provider.verify(...) … パスワード / GitHub / 将来の Google・LINE で異なる唯一の部分
 *   2. 重複チェック → 保存 → 後続施策の実行 … どの手段でも共通
 *
 * 後続処理は PostRegistrationAction の列（パイプライン）にしてある。
 * ウェルカムメール・監査ログに加え、イベント案内や招待発行などの
 * 「登録数を増やす施策」を、サービス本体を変えずに足せる（施策の拡張点）。
 *
 * 依存はすべてコンストラクタ注入（DI）。本体は実装の詳細を知らず、テスト時はモックへ差し替え可能。
 */
public final class UserRegistrationService {

    private static final Logger logger = Logger.getLogger(UserRegistrationService.class.getName());

    private final IdentityProviderRegistry providers;
    private final UserRepository userRepository;
    private final List<PostRegistrationAction> postActions;

    public UserRegistrationService(IdentityProviderRegistry providers,
                                   UserRepository userRepository,
                                   List<PostRegistrationAction> postActions) {
        this.providers = providers;
        this.userRepository = userRepository;
        this.postActions = postActions;
    }

    public RegisterResult register(AuthRequest request) {
        // 1. 本人確認（ここだけがプロバイダごとに異なる）
        IdentityProvider provider = providers.resolve(request.method());
        VerifiedIdentity identity = provider.verify(request);

        // 2. ここから先は全手段で共通の後続処理 --------------------------------
        if (userRepository.findByEmail(identity.email()) != null) {
            throw new DuplicateEmailException("このメールアドレスはすでに登録されています");
        }

        User user = new User(
                identity.email(),
                identity.displayName(),
                identity.method(),
                identity.credentialReference());
        userRepository.save(user);

        // 3. 後続施策を順に実行（ベストエフォート）
        runPostRegistrationActions(user);

        return new RegisterResult(true, user.getId(), "登録が完了しました");
    }

    /**
     * 後続施策を実行する。コア処理（保存）は完了済みなので、
     * 個々の施策が失敗しても登録自体は成功扱いとし、ログだけ残して続行する。
     */
    private void runPostRegistrationActions(User user) {
        for (PostRegistrationAction action : postActions) {
            try {
                action.execute(user);
            } catch (RuntimeException e) {
                logger.warning("登録後アクション失敗(" + action.getClass().getSimpleName()
                        + "): " + e.getMessage());
            }
        }
    }
}
