package com.youtrust.hackathon;

/** 発行済みの招待。 */
public final class Invitation {
    private final String code;
    private final String url;

    Invitation(String code, String url) {
        this.code = code;
        this.url = url;
    }

    String code() { return code; }
    String url() { return url; }
}
