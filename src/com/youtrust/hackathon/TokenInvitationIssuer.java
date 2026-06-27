package com.youtrust.hackathon;

/** ユーザーごとに一意な招待コードを発行する実装。 */
public final class TokenInvitationIssuer implements InvitationIssuer {
    @Override
    public Invitation issueFor(User user) {
        String code = "INV-" + Integer.toHexString(user.getId().hashCode()).toUpperCase();
        return new Invitation(code, "https://youtrust.example/invite/" + code);
    }
}
