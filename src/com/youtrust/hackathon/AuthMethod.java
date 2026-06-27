package com.youtrust.hackathon;

/** 登録に使える本人確認の手段。新しいソーシャルログインはここに足す。 */
public enum AuthMethod {
    PASSWORD,
    GITHUB,
    GOOGLE, // 将来追加予定（プロバイダ実装を足せば有効化できる）
    LINE    // 将来追加予定
}
