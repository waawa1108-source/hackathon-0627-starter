package com.youtrust.hackathon;

/** すでに登録済みのメールアドレスだったとき（409 相当）。 */
public final class DuplicateEmailException extends RegistrationException {
    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String message) {
        super(message);
    }
}
