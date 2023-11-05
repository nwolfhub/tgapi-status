package org.nwolfhub.botapistatus.monitor;

public class Report {
    public Long date;
    public Integer ms;
    public Boolean success;

    public Report() {}

    public Report(Long date, Integer ms, Boolean success) {
        this.date = date;
        this.ms = ms;
        this.success = success;
    }

    public Long getDate() {
        return date;
    }

    public Report setDate(Long date) {
        this.date = date;
        return this;
    }

    public Integer getMs() {
        return ms;
    }

    public Report setMs(Integer ms) {
        this.ms = ms;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Report setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public enum Type {
        mtproto,
        botapi
    }
}
