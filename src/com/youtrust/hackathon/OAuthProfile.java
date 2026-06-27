package com.youtrust.hackathon;

/** OAuthで取得した外部プロフィール。 */
public final class OAuthProfile {
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
