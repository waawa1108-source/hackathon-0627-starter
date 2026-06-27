package com.youtrust.hackathon;

/** 登録完了後にウェルカムメールを送る施策。 */
public final class WelcomeMailAction implements PostRegistrationAction {

    private final WelcomeMailer mailer;

    public WelcomeMailAction(WelcomeMailer mailer) {
        this.mailer = mailer;
    }

    @Override
    public void execute(User user) {
        mailer.sendWelcome(user);
    }
}
