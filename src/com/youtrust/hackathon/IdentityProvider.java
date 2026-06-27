package com.youtrust.hackathon;

/**
 * 「本人確認の手段」を表す抽象。この設計の拡張ポイント。
 *
 * Google や LINE を足したいときは、このインターフェースの実装クラスを1つ追加し、
 * IdentityProviderRegistry に登録するだけでよい。
 * 既存コード（UserRegistrationService など）の変更は不要 ＝ 開放閉鎖原則（OCP）。
 */
public interface IdentityProvider {

    /** このプロバイダが担当する認証手段。 */
    AuthMethod method();

    /**
     * 入力を検証し、確定した本人情報を返す。プロバイダごとに異なる唯一の処理。
     *
     * @throws AuthenticationException 本人確認に失敗したとき
     * @throws ValidationException     入力が不正なとき
     */
    VerifiedIdentity verify(AuthRequest request);
}
