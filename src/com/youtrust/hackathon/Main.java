package com.youtrust.hackathon;

import java.util.List;

/**
 * 動作確認用デモ。
 * パスワード登録と GitHub OAuth 登録が同じ register() を通り、
 * 後続施策（ウェルカムメール・監査ログ・イベント案内・招待発行）が流れることを示す。
 */
public final class Main {

    public static void main(String[] args) {
        // --- 認証手段の組み立て（手段の拡張点） ---
        IdentityProviderRegistry registry = new IdentityProviderRegistry();
        registry.register(new PasswordIdentityProvider(new DummyPasswordHasher()));
        registry.register(new GitHubIdentityProvider(new StubGitHubOAuthClient()));
        // 将来 Google を足すとき: registry.register(new GoogleIdentityProvider(...)); の1行だけ

        // --- 後続施策の組み立て（施策の拡張点） ---
        // 登録数を増やす施策は、このリストにアクションを1つ足すだけで追加できる。
        List<PostRegistrationAction> postActions = List.of(
                new WelcomeMailAction(new ConsoleWelcomeMailer()),
                new AuditLogAction(new LoggingRegistrationAuditLog()),
                new EventRecommendationAction(new StaticEventCatalog()), // ①GitHub登録者へイベント案内＋応募導線
                new ReferralInvitationAction(new TokenInvitationIssuer())); // ②本人同意ベースの招待リンク発行

        UserRegistrationService service = new UserRegistrationService(
                registry,
                new InMemoryUserRepository(),
                postActions);

        // パスワード登録（イベント案内は対象外＝GitHubのみ。招待リンクは発行される）
        System.out.println("=== パスワード登録 ===");
        RegisterResult r1 = service.register(
                AuthRequest.password("alice@example.com", "password123", "Alice"));
        System.out.println("-> success=" + r1.isSuccess() + " id=" + r1.getUserId());

        // GitHub OAuth 登録（イベント案内＋招待リンクの両方が流れる）
        System.out.println("\n=== GitHub OAuth 登録 ===");
        RegisterResult r2 = service.register(
                AuthRequest.oauth(AuthMethod.GITHUB, "dummy_auth_code"));
        System.out.println("-> success=" + r2.isSuccess() + " id=" + r2.getUserId());

        // 重複検知
        System.out.println("\n=== 重複検知 ===");
        try {
            service.register(AuthRequest.password("alice@example.com", "password123", "Alice"));
        } catch (DuplicateEmailException e) {
            System.out.println("-> " + e.getMessage());
        }
    }
}
