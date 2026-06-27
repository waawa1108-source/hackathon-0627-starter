package com.youtrust.hackathon;

/** 登録済みユーザー。認証手段に依存しない共通の表現。 */
public final class User {

    private String id;
    private final String email;
    private final String name;
    private final AuthMethod authMethod;
    private final String credentialReference;

    public User(String email, String name, AuthMethod authMethod, String credentialReference) {
        this.email = email;
        this.name = name;
        this.authMethod = authMethod;
        this.credentialReference = credentialReference;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public AuthMethod getAuthMethod() { return authMethod; }
    public String getCredentialReference() { return credentialReference; }
}
