package com.youtrust.hackathon;

/** すでに登録済みのメールアドレスだったとき（409 相当）。 */
public final class DuplicateEmailException extends RegistrationException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
