package com.youtrust.hackathon;

import java.util.List;

/** 案内対象のエンジニア向けイベント一覧を提供する。 */
public interface EventCatalog {
    List<EngineerEvent> upcomingEvents();
}
