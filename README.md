# YOUTRUST ユーザー登録 — リファクタリング（Dチーム）

「設計力を磨く！現役エンジニア伴走ハッカソン」提出物。**チーム名：Dチーム ／ メンバー：福澤亜真**

スターターの `UserRegistrationService`（登録処理が1メソッドに密結合）を、
**認証手段を後から追加できる設計**にリファクタリングした。

## お題

GitHub OAuth 登録を追加し、将来 Google / LINE も足せる構造にする。
パスワード登録と同じ後続処理（ウェルカムメール・ログ記録）を、全手段で共通に通す。

## 設計の要点

- 登録を **①本人確認（手段ごと）→ ②保存 → ③後続施策**（②③は共通）に分離
- 認証手段は `IdentityProvider` で抽象化 — 新手段は **実装1ファイル＋登録1行** で追加（既存コード無改修＝開放閉鎖原則）
- 後続施策は `PostRegistrationAction` のプラグイン列 — 登録数を増やす施策（イベント案内・招待）を本体無改修で追加
- 例外を原因別に分割（Validation / Authentication / DuplicateEmail）
- インフラ（DB / メール / ログ）は DI で注入しテスタブル化

→ 詳細は **DESIGN.md**（全体図つき）と **DECISIONS.md**（ADR＋設計判断の言語化）

## ファイル構成

```
src/com/youtrust/hackathon/   … Java ソース一式（package com.youtrust.hackathon）
DESIGN.md                     … 設計ドキュメント
DECISIONS.md                  … 設計判断ログ（ADR）
```

## ビルド & 実行

```
javac -d out $(find src -name "*.java")
java -cp out com.youtrust.hackathon.Main
```

`Main` はパスワード登録・GitHub OAuth 登録・重複検知のデモを実行する。

---

主催：YOUTRUST ／ Engineering Partners：dip / Timee / STORES
