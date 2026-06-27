package com.youtrust.hackathon;

/** OAuthプロバイダとの通信の抽象（GitHub / Google / LINE で使い回せる）。 */
public interface OAuthClient {
    OAuthProfile exchange(String authorizationCode);
}
