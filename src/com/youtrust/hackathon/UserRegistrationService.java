package com.youtrust.hackathon;

/**
 * ユーザー登録のオーケストレーション。
 *
 * 設計の核：登録を「本人確認（プロバイダ依存）」と「後続処理（全プロバイダ共通）」に分離する。
 *   1. provider.verify(...)  … パスワード / GitHub / 将来の Google・LINE で異なる唯一の部分
 *   2. 重複チェック → 保存 → ウェルカムメール → 監査ログ … どの手段でも完全に同じ
 *
 * これにより、新しいログイン手段は IdentityProvider を1つ足してレジストリに登録するだけで、
 * この共通の後続処理にそのまま乗る（お題の「Google/LINEも後から追加できる構造」を満たす）。
 *
 * 依存（Repository / Mailer / AuditLog / プロバイダ群）はコンストラクタで注入する。
 * これにより本体は実装の詳細を知らず、テスト時はモックへ差し替えられる。
 */
public final class UserRegistrationService {

    private final IdentityProviderRegistry providers;
    private final UserRepository userRepository;
    private final WelcomeMailer welcomeMailer;
    private final RegistrationAuditLog auditLog;

    public UserRegistrationService(IdentityProviderRegistry providers,
                                   UserRepository userRepository,
                                   WelcomeMailer welcomeMailer,
                                   RegistrationAuditLog auditLog) {
        this.providers = providers;
        this.userRepository = userRepository;
        this.welcomeMailer = welcomeMailer;
        this.auditLog = auditLog;
    }

    public RegisterResult register(AuthRequest request) {
        // 1. 本人確認（ここだけがプロバイダごとに異なる）
        IdentityProvider provider = providers.resolve(request.method());
        VerifiedIdentity identity = provider.verify(request);

        // 2. ここから先は全プロバイダ共通の後続処理 ------------------------------
        if (userRepository.findByEmail(identity.email()) != null) {
            throw new DuplicateEmailException("このメールアドレスはすでに登録されています");
        }

        User user = new User(
                identity.email(),
                identity.displayName(),
                identity.method(),
                identity.credentialReference());
        userRepository.save(user);

        welcomeMailer.sendWelcome(user);
        auditLog.recordRegistration(user);

        return new RegisterResult(true, user.getId(), "登録が完了しました");
    }
}
