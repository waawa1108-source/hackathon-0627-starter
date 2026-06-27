package com.youtrust.hackathon;

import java.util.logging.Logger;

/** 登録イベントの記録。 */
public interface RegistrationAuditLog {
    void recordRegistration(User user);
}

/** java.util.logging を使う実装。 */
final class LoggingRegistrationAuditLog implements RegistrationAuditLog {

    private static final Logger logger = Logger.getLogger(LoggingRegistrationAuditLog.class.getName());

    @Override
    public void recordRegistration(User user) {
        logger.info("ユーザー登録完了: " + user.getEmail() + " (方法=" + user.getAuthMethod() + ")");
    }
}
