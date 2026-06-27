package com.youtrust.hackathon;

/** メール＋パスワードによる本人確認。 */
public final class PasswordIdentityProvider implements IdentityProvider {

    private final PasswordHasher hasher;

    public PasswordIdentityProvider(PasswordHasher hasher) {
        this.hasher = hasher;
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.PASSWORD;
    }

    @Override
    public VerifiedIdentity verify(AuthRequest request) {
        String email = request.email();
        String password = request.password();
        String name = request.name();

        if (email == null || !email.contains("@")) {
            throw new ValidationException("メールアドレスが無効です");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("パスワードは8文字以上必要です");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("名前は必須です");
        }

        // 生パスワードはここで境界の外に出さない。ハッシュ済みの参照だけを後続へ渡す。
        return new VerifiedIdentity(email, name.trim(), AuthMethod.PASSWORD, hasher.hash(password));
    }
}

/** パスワードハッシュ化の抽象。本番は bcrypt / argon2 などに差し替える。 */
interface PasswordHasher {
    String hash(String rawPassword);
}

/** ハッカソン用の簡易ハッシュ（本番では絶対に使わない）。 */
final class DummyPasswordHasher implements PasswordHasher {
    @Override
    public String hash(String rawPassword) {
        return Integer.toHexString(rawPassword.hashCode()) + "_hashed";
    }
}
