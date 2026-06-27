package com.youtrust.hackathon;

/**
 * 登録に関する例外の基底。
 * 呼び出し側が原因ごとに処理を分けられるよう、用途別のサブクラスで粒度を分ける
 * （元コードは何でも Exception / IllegalArgumentException で粒度が粗かった）。
 */
public abstract class RegistrationException extends RuntimeException {
    protected RegistrationException(String message) {
        super(message);
    }
}
