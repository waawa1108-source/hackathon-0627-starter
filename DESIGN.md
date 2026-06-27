# 設計ドキュメント

> チーム名：（当日記入）
> メンバー：（当日記入）

---

## 1. 課題の整理

スターターの `UserRegistrationService.register()` に、登録の全責務が1メソッドで密結合していた。

- **認証手段がパスワード固定**。`input.getPassword()` 前提で、GitHub OAuth を差し込む拡張点がない。
- **単一責任の崩壊**。バリデーション・重複チェック・ハッシュ化・DB保存・メール送信・ログ記録が同居。
- **テスト困難**。`Database` / `EmailClient` をメソッド内で直接 `new` していて差し替えられない。
- **例外が粗粒度**。`throws Exception` と `IllegalArgumentException` のみで、原因別に扱えない。

→ お題は「GitHub OAuth を足し、将来 Google/LINE も足せる構造に。後続処理（メール・ログ）は共通で通す」。
　つまり **「手段ごとに違う部分」と「全手段で共通の部分」を分離できるか** が論点。

## 2. 設計方針

登録を **2フェーズに分離**する。

```
① 本人確認フェーズ（手段ごとに異なる）
   PasswordIdentityProvider / GitHubIdentityProvider / （将来）Google・LINE
        │  VerifiedIdentity（確定した本人情報）を返す  ← プロバイダ非依存の境界面
        ▼
② 後続処理フェーズ（全手段で完全に共通）
   重複チェック → 保存 → ウェルカムメール → 監査ログ
```

- ①を `IdentityProvider` インターフェースで抽象化（**拡張ポイント**）。
- ②を `UserRegistrationService` に集約。サービスは「①を呼ぶ→②を流す」だけに痩せる。
- インフラ（DB/メール/ログ）はインターフェース化し、**コンストラクタ注入（DI）**で差し替え可能に。

## 3. クラス・メソッド構成

```
UserRegistrationService          // オーケストレーション（①を呼び②を流すだけ）
├── IdentityProviderRegistry     // 認証手段 → プロバイダ を引く登録簿
│   └── IdentityProvider (IF)    // ★拡張ポイント
│       ├── PasswordIdentityProvider   (+ PasswordHasher)
│       └── GitHubIdentityProvider     (+ OAuthClient / OAuthProfile)
├── VerifiedIdentity             // 認証済みの本人情報（①と②の境界）
├── UserRepository (IF)          // └ InMemoryUserRepository
├── WelcomeMailer (IF)           // └ ConsoleWelcomeMailer
├── RegistrationAuditLog (IF)    // └ LoggingRegistrationAuditLog
└── RegistrationException (基底)  // ├ ValidationException
                                 // ├ AuthenticationException
                                 // └ DuplicateEmailException
```

## 4. 工夫したポイント

- **拡張点を1つに絞った**：手段を増やすときに触る場所が `IdentityProvider` 実装の追加＋レジストリ登録の1行だけ。`if`/`switch` を増やさない（OCP）。
- **`VerifiedIdentity` という境界面**：パスワードか OAuth かを②に一切漏らさない。生パスワードは `verify()` 内でハッシュ化し、後続にはハッシュ参照しか渡さない。
- **DI でテスタブルに**：サービスは実装の詳細を知らず、テスト時はモックへ差し替えられる。
- **例外を原因別に分割**：呼び出し側が 400/401/409 へマッピングできる粒度に。

## 5. できなかったこと・今後の改善点

- OAuth 通信は `OAuthClient` のスタブ実装（外部通信なし）。本番は code→token→GET /user を実装して差し替える。
- パスワードハッシュはダミー。本番は bcrypt / argon2 に差し替え（インターフェース化済みなので実装追加のみ）。
- 同一メールで複数手段をひも付ける「アカウント連携」は未対応。`User` に複数 credential を持たせる拡張で対応可能。
- 永続化はメモリ実装。`UserRepository` の RDB 実装を追加すれば本体は無改修。
