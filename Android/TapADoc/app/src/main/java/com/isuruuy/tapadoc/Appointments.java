package com.isuruuy.tapadoc;

public class Appointments {

    String date;
    String time;
    String slotid;
    String docId;
    String patientid;

    public Appointments() {
    }

    public Appointments(String date, String time, String slotid, String docId, String patientid) {
        this.date = date;
        this.time = time;
        this.slotid = slotid;
        this.docId = docId;
        this.patientid = patientid;
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

    public String getSlotid() {
        return slotid;
    }

    public void setSlotid(String slotid) {
        this.slotid = slotid;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }
}
