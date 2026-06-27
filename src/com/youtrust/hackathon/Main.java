package com.youtrust.hackathon;

/**
 * 動作確認用デモ。
 * パスワード登録と GitHub OAuth 登録が、同じ register() / 同じ後続処理を通ることを示す。
 */
public final class Main {

    public static void main(String[] args) {
        // --- 依存の組み立て（本番は DI コンテナ。ここでは手組み） ---
        IdentityProviderRegistry registry = new IdentityProviderRegistry();
        registry.register(new PasswordIdentityProvider(new DummyPasswordHasher()));
        registry.register(new GitHubIdentityProvider(new StubGitHubOAuthClient()));
        // 将来 Google を足すとき: registry.register(new GoogleIdentityProvider(...)); の1行だけ

        UserRegistrationService service = new UserRegistrationService(
                registry,
                new InMemoryUserRepository(),
                new ConsoleWelcomeMailer(),
                new LoggingRegistrationAuditLog());

        // パスワード登録
        RegisterResult r1 = service.register(
                AuthRequest.password("alice@example.com", "password123", "Alice"));
        System.out.println("PASSWORD登録 -> success=" + r1.isSuccess() + " id=" + r1.getUserId());

        // GitHub OAuth 登録（同じ register() / 同じウェルカムメール・ログ）
        RegisterResult r2 = service.register(
                AuthRequest.oauth(AuthMethod.GITHUB, "dummy_auth_code"));
        System.out.println("GITHUB登録   -> success=" + r2.isSuccess() + " id=" + r2.getUserId());

        // 重複検知の確認（同じメールでもう一度）
        try {
            service.register(AuthRequest.password("alice@example.com", "password123", "Alice"));
        } catch (DuplicateEmailException e) {
            System.out.println("重複検知     -> " + e.getMessage());
        }
    }
}
