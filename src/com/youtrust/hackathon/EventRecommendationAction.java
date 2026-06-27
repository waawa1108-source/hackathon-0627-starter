package com.youtrust.hackathon;

import java.util.List;

/**
 * GitHub で登録した（＝エンジニア度が高い）ユーザーに、直近のエンジニア向けイベントを案内し、
 * アカウント作成と同じ流れで応募できる導線（応募URL）を提示する施策。
 *
 * 「YOUTRUST のエンジニアユーザー登録数を増やす」という目的に向けた後続施策の一例。
 * 認証手段（AuthMethod）を“エンジニアらしさ”のシグナルとして使い、案内対象を絞る。
 */
public final class EventRecommendationAction implements PostRegistrationAction {

    private final EventCatalog eventCatalog;

    public EventRecommendationAction(EventCatalog eventCatalog) {
        this.eventCatalog = eventCatalog;
    }

    @Override
    public void execute(User user) {
        // GitHub 登録者に絞って案内する（手段がエンジニア度のシグナル）。
        if (user.getAuthMethod() != AuthMethod.GITHUB) {
            return;
        }
        List<EngineerEvent> events = eventCatalog.upcomingEvents();
        if (events.isEmpty()) {
            return;
        }
        System.out.println("[Event] " + user.getName()
                + " さんへ：エンジニア向けイベントのご案内（アカウントそのままで応募できます）");
        for (EngineerEvent event : events) {
            // 応募URLにユーザーIDを載せ、ワンクリックで本人として応募できるようにする。
            String applyUrl = event.applyUrl() + "?applicant=" + user.getId();
            System.out.println("  - " + event.title() + "（" + event.date() + "） 応募: " + applyUrl);
        }
    }
}

/** 案内対象のエンジニア向けイベント一覧を提供する。 */
interface EventCatalog {
    List<EngineerEvent> upcomingEvents();
}

/** 直近イベントの固定実装（本番はイベント管理サービスに差し替え）。 */
final class StaticEventCatalog implements EventCatalog {
    @Override
    public List<EngineerEvent> upcomingEvents() {
        return List.of(
                new EngineerEvent("YOUTRUST Tech Meetup #5", "2026-07-15", "https://youtrust.example/events/5/apply"),
                new EngineerEvent("リファクタリング・ナイト", "2026-07-22", "https://youtrust.example/events/6/apply"));
    }
}

/** エンジニア向けイベント。 */
final class EngineerEvent {
    private final String title;
    private final String date;
    private final String applyUrl;

    EngineerEvent(String title, String date, String applyUrl) {
        this.title = title;
        this.date = date;
        this.applyUrl = applyUrl;
    }

    String title() { return title; }
    String date() { return date; }
    String applyUrl() { return applyUrl; }
}
