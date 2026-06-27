package com.youtrust.hackathon;


/** 登録イベントの記録。 */
public interface RegistrationAuditLog {
    void recordRegistration(User user);
}
