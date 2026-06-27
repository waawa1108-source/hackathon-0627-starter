package com.youtrust.hackathon;

/** ハッカソン用の簡易ハッシュ（本番では絶対に使わない）。 */
public final class DummyPasswordHasher implements PasswordHasher {
    @Override
    public String hash(String rawPassword) {
        return Integer.toHexString(rawPassword.hashCode()) + "_hashed";
    }
}
