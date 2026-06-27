package com.youtrust.hackathon;

/**
 * 本人確認が済んだ結果。
 *
 * どのプロバイダで認証したかに関わらず、後続処理（保存・メール・ログ）は
 * この確定済み情報だけを見れば完結する ＝ プロバイダ非依存の「境界面」。
 */
public final class VerifiedIdentity {

    private final String email;
    private final String displayName;
    private final AuthMethod method;
    private final String credentialReference; // パスワードハッシュ or 外部ID。保存用。

    public VerifiedIdentity(String email, String displayName, AuthMethod method, String credentialReference) {
        this.email = email;
        this.displayName = displayName;
        this.method = method;
        this.credentialReference = credentialReference;
    }

    public String email() { return email; }
    public String displayName() { return displayName; }
    public AuthMethod method() { return method; }
    public String credentialReference() { return credentialReference; }
}
