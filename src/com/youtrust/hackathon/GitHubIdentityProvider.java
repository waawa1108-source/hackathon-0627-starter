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

/** OAuthプロバイダとの通信の抽象（GitHub / Google / LINE で使い回せる）。 */
interface OAuthClient {
    OAuthProfile exchange(String authorizationCode);
}

/** OAuthで取得した外部プロフィール。 */
final class OAuthProfile {
    private final String externalId;
    private final String email;
    private final String name;

    OAuthProfile(String externalId, String email, String name) {
        this.externalId = externalId;
        this.email = email;
        this.name = name;
    }

    String externalId() { return externalId; }
    String email() { return email; }
    String name() { return name; }
}

/** GitHub OAuth通信のスタブ（外部通信なし）。本物のGitHub APIに差し替え可能。 */
final class StubGitHubOAuthClient implements OAuthClient {
    @Override
    public OAuthProfile exchange(String authorizationCode) {
        // 本来は code -> access token -> GET /user で取得する
        return new OAuthProfile("gh_12345", "octocat@github.com", "The Octocat");
    }
}
