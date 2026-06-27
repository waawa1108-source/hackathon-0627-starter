package com.youtrust.hackathon;

import java.util.List;

/** 直近イベントの固定実装（本番はイベント管理サービスに差し替え）。 */
public final class StaticEventCatalog implements EventCatalog {
    @Override
    public List<EngineerEvent> upcomingEvents() {
        return List.of(
                new EngineerEvent("YOUTRUST Tech Meetup #5", "2026-07-15", "https://youtrust.example/events/5/apply"),
                new EngineerEvent("リファクタリング・ナイト", "2026-07-22", "https://youtrust.example/events/6/apply"));
    }
}
