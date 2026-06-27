package com.youtrust.hackathon;

/**
 * GitHub OAuth による本人確認。
 *
 * 認可コードをアクセストークンに交換し、GitHub のプロフィールからメール・氏名を取得する想定。
 * 外部通信は OAuthClient に隔離してあるので、Google / LINE もこの仕組みを再利用できる。
 */
public final class GitHubIdentityProvider implements IdentityProvider {

    private final OAuthClient oauthClient;

    public GitHubIdentityProvider(OAuthClient oauthClient) {
        this.oauthClient = oauthClient;
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.GITHUB;
    }

    @Override
    public VerifiedIdentity verify(AuthRequest request) {
        if (request.oauthCode() == null || request.oauthCode().isBlank()) {
            throw new AuthenticationException("GitHubの認可コードがありません");
        }
        OAuthProfile profile = oauthClient.exchange(request.oauthCode());
        if (profile.email() == null || !profile.email().contains("@")) {
            throw new AuthenticationException("GitHubアカウントから有効なメールを取得できませんでした");
        }
        // パスワードは持たず、外部IDを認証情報の参照として保存する。
        return new VerifiedIdentity(
                profile.email(), profile.name(), AuthMethod.GITHUB, "github:" + profile.externalId());
    }
}

