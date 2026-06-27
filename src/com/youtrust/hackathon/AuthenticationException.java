package com.youtrust.hackathon;

/** 本人確認に失敗したとき（401 相当）。 */
public final class AuthenticationException extends RegistrationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
