package com.example.stayfocus;

public class HistoryRecord {
    private String activityName;
    private String sessionType;
    private String date;
    private String time;
    private String loopInfo;

    public HistoryRecord(String activityName, String sessionType, String date, String time, String loopInfo) {
        this.activityName = activityName;
        this.sessionType = sessionType;
        this.date = date;
        this.time = time;
        this.loopInfo = loopInfo;
    }

    // Getters
    public String getActivityName() { return activityName; }
    public String getSessionType() { return sessionType; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLoopInfo() { return loopInfo; }
}