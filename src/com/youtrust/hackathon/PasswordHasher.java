package com.youtrust.hackathon;

/** パスワードハッシュ化の抽象。本番は bcrypt / argon2 などに差し替える。 */
public interface PasswordHasher {
    String hash(String rawPassword);
}
