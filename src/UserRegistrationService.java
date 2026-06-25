package com.youtrust.hackathon;

import java.util.logging.Logger;

/**
 * ユーザー登録サービス（スターターコード）
 *
 * TODO: このクラスをリファクタリングしてください。
 * どう設計するか、なぜそう設計するかを DESIGN.md と DECISIONS.md に記録してください。
 */
public class UserRegistrationService {

    private static final Logger logger = Logger.getLogger(UserRegistrationService.class.getName());

    // データベース接続（簡略化のためモック）
    private final Database database = new Database();

    // メール送信（簡略化のためモック）
    private final EmailClient emailClient = new EmailClient();

    /**
     * ユーザーを登録する
     *
     * @param input 登録情報
     * @return 登録結果
     * @throws Exception 何か問題が起きたとき
     */
    public RegisterResult register(RegisterInput input) throws Exception {

        // バリデーション
        if (input.getEmail() == null || !input.getEmail().contains("@")) {
            throw new IllegalArgumentException("メールアドレスが無効です");
        }
        if (input.getPassword() == null || input.getPassword().length() < 8) {
            throw new IllegalArgumentException("パスワードは8文字以上必要です");
        }
        if (input.getName() == null || input.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }

        // 重複チェック
        if (database.findByEmail(input.getEmail()) != null) {
            throw new IllegalArgumentException("このメールアドレスはすでに登録されています");
        }

        // パスワードハッシュ化（簡略化）
        String hashedPassword = input.getPassword() + "_hashed";

        // DBに保存
        User user = new User();
        user.setEmail(input.getEmail());
        user.setName(input.getName());
        user.setPassword(hashedPassword);
        database.save(user);

        // 確認メール送信
        String subject = "【ハッカソン】登録完了のお知らせ";
        String body = input.getName() + " 様\n\nご登録ありがとうございます。";
        emailClient.send(input.getEmail(), subject, body);

        // ログ記録
        logger.info("ユーザー登録完了: " + input.getEmail());

        return new RegisterResult(true, user.getId(), "登録が完了しました");
    }


    // ---- 以下はモッククラス（変更不要） ----

    static class Database {
        public User findByEmail(String email) { return null; }
        public void save(User user) { user.setId("user_" + System.currentTimeMillis()); }
    }

    static class EmailClient {
        public void send(String to, String subject, String body) {
            System.out.println("Email sent to: " + to);
        }
    }

    static class User {
        private String id;
        private String email;
        private String name;
        private String password;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class RegisterInput {
        private String email;
        private String password;
        private String name;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class RegisterResult {
        private final boolean success;
        private final String userId;
        private final String message;
        public RegisterResult(boolean success, String userId, String message) {
            this.success = success;
            this.userId = userId;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public String getUserId() { return userId; }
        public String getMessage() { return message; }
    }
}
