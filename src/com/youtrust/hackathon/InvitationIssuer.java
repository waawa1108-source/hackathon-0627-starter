package com.youtrust.hackathon;

/** 招待リンクを発行する。 */
public interface InvitationIssuer {
    Invitation issueFor(User user);
}
