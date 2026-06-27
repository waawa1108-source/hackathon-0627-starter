package com.youtrust.hackathon;

/** GitHub OAuth通信のスタブ（外部通信なし）。本物のGitHub APIに差し替え可能。 */
public final class StubGitHubOAuthClient implements OAuthClient {
    @Override
    public OAuthProfile exchange(String authorizationCode) {
        // 本来は code -> access token -> GET /user で取得する
        return new OAuthProfile("gh_12345", "octocat@github.com", "The Octocat");
    }
}
