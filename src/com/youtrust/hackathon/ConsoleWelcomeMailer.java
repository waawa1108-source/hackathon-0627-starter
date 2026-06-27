package com.youtrust.hackathon;

/** コンソール出力の実装（ハッカソン用）。本番は実メール送信に差し替える。 */
public final class ConsoleWelcomeMailer implements WelcomeMailer {
    @Override
    public void sendWelcome(User user) {
        System.out.println("[Mail] To=" + user.getEmail()
                + " 件名=【YOUTRUST】ご登録ありがとうございます"
                + " 本文=" + user.getName() + " 様、ご登録ありがとうございます。");
    }
}
