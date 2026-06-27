package com.youtrust.hackathon;


/** ユーザー永続化の抽象。本番は RDB 実装に差し替える。 */
public interface UserRepository {
    User findByEmail(String email);
    void save(User user);
}
