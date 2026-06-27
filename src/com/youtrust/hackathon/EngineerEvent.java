package com.youtrust.hackathon;

/** エンジニア向けイベント。 */
public final class EngineerEvent {
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
