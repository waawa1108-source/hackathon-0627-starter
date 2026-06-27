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

