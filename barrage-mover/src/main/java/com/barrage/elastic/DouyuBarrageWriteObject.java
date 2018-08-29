package com.barrage.elastic;

public class DouyuBarrageWriteObject implements EsWriteObject {

    @EsFiled
    private String nn;
    @EsFiled
    private String uid;
    @EsFiled
    private String text;

    public String getNn() {
        return nn;
    }

    public void setNn(String nn) {
        this.nn = nn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
