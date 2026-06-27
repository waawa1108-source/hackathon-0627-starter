package com.youtrust.hackathon;

/** 登録完了を監査ログに記録する施策。 */
public final class AuditLogAction implements PostRegistrationAction {

    private final RegistrationAuditLog auditLog;

    public AuditLogAction(RegistrationAuditLog auditLog) {
        this.auditLog = auditLog;
    }

    @Override
    public void execute(User user) {
        auditLog.recordRegistration(user);
    }
}
