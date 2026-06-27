package com.youtrust.hackathon;

import java.util.logging.Logger;

/** java.util.logging を使う実装。 */
public final class LoggingRegistrationAuditLog implements RegistrationAuditLog {

    private static final Logger logger = Logger.getLogger(LoggingRegistrationAuditLog.class.getName());

    @Override
    public void recordRegistration(User user) {
        logger.info("ユーザー登録完了: " + user.getEmail() + " (方法=" + user.getAuthMethod() + ")");
    }
}
