package com.youtrust.hackathon;

/**
 * 登録した本人に「招待リンク」を発行する施策（エンジニアの知人を呼び込むバイラル導線）。
 *
 * ★設計判断：GitHub のフォロワー情報を無断取得して知人へ自動送信する案は採用しない。
 *   本人の同意なく第三者へマーケメールを送る行為は、特定電子メール法・GDPR・GitHub 規約に違反し、
 *   サービスへの信頼を損なう（登録数を増やす施策ほど、信頼を壊すと逆効果になる）。
 *   代わりに「本人専用の招待リンクを発行し、誰に送るかは本人が決める」同意ベースの設計にした。
 */
public final class ReferralInvitationAction implements PostRegistrationAction {

    private final InvitationIssuer invitationIssuer;

    public ReferralInvitationAction(InvitationIssuer invitationIssuer) {
        this.invitationIssuer = invitationIssuer;
    }

    @Override
    public void execute(User user) {
        Invitation invitation = invitationIssuer.issueFor(user);
        System.out.println("[Referral] " + user.getName()
                + " さん専用の招待リンクを発行しました: " + invitation.url()
                + " （誘いたい知人エンジニアに、あなたご自身で共有してください）");
    }
}

/** 招待リンクを発行する。 */
interface InvitationIssuer {
    Invitation issueFor(User user);
}

/** ユーザーごとに一意な招待コードを発行する実装。 */
final class TokenInvitationIssuer implements InvitationIssuer {
    @Override
    public Invitation issueFor(User user) {
        String code = "INV-" + Integer.toHexString(user.getId().hashCode()).toUpperCase();
        return new Invitation(code, "https://youtrust.example/invite/" + code);
    }
}

/** 発行済みの招待。 */
final class Invitation {
    private final String code;
    private final String url;

    Invitation(String code, String url) {
        this.code = code;
        this.url = url;
    }

    String code() { return code; }
    String url() { return url; }
}
