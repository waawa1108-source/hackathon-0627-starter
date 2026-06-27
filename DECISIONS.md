# 設計判断ログ

> チーム名：（当日記入）

---

## 判断ログ一覧

### [ADR-001] 認証手段を IdentityProvider で抽象化した

**状況**：
お題は「GitHub OAuth を追加。将来 Google/LINE も足せる構造に」。元コードは `register()` がパスワード固定で、手段ごとに `if (type == ...)` を増やすか、メソッドをコピーするしかなかった。

**決定**：
「本人確認」を `IdentityProvider`（`verify(AuthRequest) -> VerifiedIdentity`）インターフェースに切り出し、手段ごとに実装クラスを作る。手段→実装は `IdentityProviderRegistry` で引く。

**理由**：
手段が増えても本体ロジックが膨らまない。新手段は実装1ファイル追加＋登録1行で済み、既存コードを触らない（開放閉鎖原則 / OCP）。

**トレードオフ**：
分岐 `if/switch` で書く方が初手は速いが、手段が増えるほど分岐が各所に散る。今回は「将来 Google/LINE を足す」が明示されているので、抽象化のコストを払う価値があると判断した。

---

### [ADR-002] 「本人確認」と「後続処理」を VerifiedIdentity で分離した

**状況**：
パスワードでも OAuth でも、登録後の処理（重複チェック・保存・ウェルカムメール・ログ）は同じにしたい。手段ごとに後続処理を書くと重複し、片方だけ直す事故が起きる。

**決定**：
`verify()` の出力を `VerifiedIdentity`（email / 表示名 / 認証手段 / 認証情報の参照）に統一。`UserRegistrationService` はこの確定情報だけを見て共通の後続処理を流す。

**理由**：
②（後続処理）が①（手段）に依存しなくなる。後続処理は1か所だけ＝仕様変更（例：メール文面）も1か所で済む。

**トレードオフ**：
境界用の型を1つ増やす分クラスは増えるが、共通化と引き換えなら妥当。

---

### [ADR-003] 例外を原因別のサブクラスに分割した

**状況**：
元コードは `throws Exception` と `IllegalArgumentException` のみ。呼び出し側が「入力ミス」「本人確認失敗」「重複」を区別できない。

**決定**：
`RegistrationException`（基底）の下に `ValidationException`(400) / `AuthenticationException`(401) / `DuplicateEmailException`(409) を定義。非チェック例外にしてシグネチャを汚さない。

**理由**：
API 層で原因別に HTTP ステータスへマッピングできる。型を見れば何が起きうるか分かる（堅牢性・可読性）。

**トレードオフ**：
例外クラスが増えるが、原因の区別という価値の方が大きい。

---

## 提出フォーム必須項目への回答

**選んだ設計アプローチ**：
登録を「本人確認（手段依存）」と「後続処理（全手段共通）」の2フェーズに分け、前者を `IdentityProvider` で戦略化（Strategy パターン）。インフラは DI で注入。

**なぜその設計を選んだか**：
お題の核心が「手段を後から足せること」だから。拡張ポイントを `IdentityProvider` 1点に集約し、共通処理を1か所に固定することで、追加コストを最小化した。

**切ったこと・やらなかったこと**：
- OAuth 実通信・実ハッシュ（bcrypt）はスタブ／ダミー。インターフェースは切ってあるので差し替えのみ。
- アカウント連携（同一メールで複数手段）は今回スコープ外。
- DI コンテナ（Spring 等）は使わず手組み。規模的に過剰なため。

**Google や LINE の OAuth も後から追加できる構造か（Yes/No ＋ どこに何行追加すれば足せるか）**：
**Yes。** 2ステップで足せる。
1. `GoogleIdentityProvider implements IdentityProvider` を新規1ファイル作成（`GitHubIdentityProvider` と同型。`OAuthClient` は再利用可）。
2. 組み立て箇所（`Main` / 本番なら DI 設定）に `registry.register(new GoogleIdentityProvider(...));` を1行追加。
→ `UserRegistrationService` など既存コードは無改修。`AuthMethod` に `GOOGLE` は定義済み。

**一番悩んだポイント**：
パスワードと OAuth で入力（email+password か 認可コードか）が違う点をどう一本化するか。`Map` で渡すと型安全が崩れるため、`AuthRequest` に用途別ファクトリ（`AuthRequest.password(...)` / `AuthRequest.oauth(...)`）を用意し、生成時点で「どの手段の入力か」を固定する形に落とした。
