package com.youtrust.hackathon;

/**
 * 登録完了後に実行する「施策」。
 *
 * ウェルカムメール・監査ログに加え、イベント案内・招待発行など
 * 「登録数を増やすための後続施策」をこの単位でプラグイン化する。
 * 新しい施策は、このインターフェースの実装を1つ足してリストに追加するだけでよい
 * （IdentityProvider が「認証手段の拡張点」なら、こちらは「施策の拡張点」）。
 *
 * 施策はベストエフォート：1つ失敗しても、保存済みの登録自体は成功扱いとする。
 */
public interface PostRegistrationAction {
    void execute(User user);
}
