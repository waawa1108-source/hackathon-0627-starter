package com.youtrust.hackathon;

/**
 * 登録リクエスト。
 *
 * パスワード登録とOAuth登録では必要な情報が異なるため、用途別のファクトリメソッドで生成する。
 * Map や null 散乱を避け、「どの手段の入力なのか」を生成時点で型と名前で固定するのが狙い。
 */
public final class AuthRequest {

    private final AuthMethod method;
    private final String email;     // パスワード登録で利用
    private final String password;  // パスワード登録で利用
    private final String name;      // パスワード登録で利用
    private final String oauthCode; // OAuth登録で利用（フロントから渡る認可コード）

    private AuthRequest(AuthMethod method, String email, String password, String name, String oauthCode) {
        this.method = method;
        this.email = email;
        this.password = password;
        this.name = name;
        this.oauthCode = oauthCode;
    }

    /** メール＋パスワードでの登録リクエストを作る。 */
    public static AuthRequest password(String email, String password, String name) {
        return new AuthRequest(AuthMethod.PASSWORD, email, password, name, null);
    }

    /** OAuth（GitHub など）での登録リクエストを作る。 */
    public static AuthRequest oauth(AuthMethod method, String oauthCode) {
        return new AuthRequest(method, null, null, null, oauthCode);
    }

    public AuthMethod method() { return method; }
    public String email() { return email; }
    public String password() { return password; }
    public String name() { return name; }
    public String oauthCode() { return oauthCode; }
}
