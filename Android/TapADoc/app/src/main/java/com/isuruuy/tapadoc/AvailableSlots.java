package com.isuruuy.tapadoc;

public class AvailableSlots {

    String date;
    String time;
    String id;
    String docid;

    public AvailableSlots() {
    }

    public AvailableSlots(String date, String time, String id, String docid) {
        this.date = date;
        this.time = time;
        this.id = id;
        this.docid = docid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }
}
