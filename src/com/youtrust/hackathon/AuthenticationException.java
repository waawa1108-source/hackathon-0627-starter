package com.youtrust.hackathon;

/** 本人確認に失敗したとき（401 相当）。 */
public final class AuthenticationException extends RegistrationException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
