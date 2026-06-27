package com.youtrust.hackathon;

/** 入力値が不正なとき（400 相当）。 */
public final class ValidationException extends RegistrationException {
    public ValidationException(String message) {
        super(message);
    }
}
