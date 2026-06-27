package com.youtrust.hackathon;

import java.util.HashMap;
import java.util.Map;

/** ユーザー永続化の抽象。本番は RDB 実装に差し替える。 */
public interface UserRepository {
    User findByEmail(String email);
    void save(User user);
}

/** メモリ実装（ハッカソン用）。 */
final class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byEmail = new HashMap<>();
    private long seq = 0;

    @Override
    public User findByEmail(String email) {
        return byEmail.get(email);
    }

    @Override
    public void save(User user) {
        user.setId("user_" + (++seq));
        byEmail.put(user.getEmail(), user);
    }
}
