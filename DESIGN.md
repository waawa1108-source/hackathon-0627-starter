# 設計ドキュメント

> チーム名：Dチーム
> メンバー：福澤亜真

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

## 3. クラス構成と関係（全体図）

登録は `register()` が **①本人確認 → ②保存 → ③後続施策** の3ステップを順に流すだけ。
手段ごとに変わるのは①のみで、②③は全手段で共通。だから——
新しい認証手段は **★①** へ、登録数を増やす新施策は **★②** へ足すだけで、本体は無改修で済む。

```
               ① 本人確認          ② 保存              ③ 後続施策
              （手段ごと / ★①）     （共通）            （共通・複数 / ★②）

 AuthRequest ─► IdentityProvider ─► UserRepository ─► PostRegistrationAction ─► RegisterResult
  (入力)            │
              VerifiedIdentity ← ①と②の境界（手段に依存しない確定情報）
```

各ステップの抽象（インターフェース）と実装:

| ステップ | 抽象 | 実装 |
|---------|------|------|
| ① 本人確認 ★① | `IdentityProvider` | `PasswordIdentityProvider` / `GitHubIdentityProvider`（将来 Google・LINE） |
| ② 保存 | `UserRepository` | `InMemoryUserRepository` |
| ③ 後続施策 ★② | `PostRegistrationAction` | `WelcomeMail` / `AuditLog` / `EventRecommendation` / `ReferralInvitation` |
| 例外 | `RegistrationException` | `ValidationException`(400) / `AuthenticationException`(401) / `DuplicateEmailException`(409) |

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

## 6. 目的への接続：登録数を増やす施策（差別化）

このリファクタリングは「動く形に整える」だけでなく、お題の目的＝**エンジニアユーザーの登録数を増やす**ための土台にしている。後続処理を `PostRegistrationAction` のリストにしたことで、登録促進の施策を**サービス本体を変えずに足せる**。実装した施策は2つ:

- **① エンジニアイベント案内（`EventRecommendationAction`）**：GitHub 登録者は「エンジニア度が高い」というシグナル。登録直後に直近のエンジニア向けイベントを案内し、アカウントそのままで応募できる導線（応募URLにユーザーIDを付与）を出す。
- **② 同意ベースの招待（`ReferralInvitationAction`）**：本人専用の招待リンクを発行し、知人エンジニアを呼べるようにする。

**あえて切った設計（②の判断）**：「GitHub のフォロワー情報を取得して知人へ自動でDM/メールを送る」案は**採用しなかった**。本人の同意なき第三者への送信は、特定電子メール法・GDPR・GitHub 規約に違反し、サービスへの信頼を毀損する。**登録数を増やす施策ほど、信頼を壊すと逆効果**になるため、「誰に送るかは本人が決める」同意ベースに倒した。これは「成長」と「信頼・コンプライアンス」のトレードオフを意識した判断である。
