package com.youtrust.hackathon;

/** 登録後のウェルカムメール送信。 */
public interface WelcomeMailer {
    void sendWelcome(User user);
}
