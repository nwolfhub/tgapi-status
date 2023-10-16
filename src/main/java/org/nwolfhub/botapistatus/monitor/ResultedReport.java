package org.nwolfhub.botapistatus.monitor;

import java.io.Serializable;
import java.util.Date;

public class ResultedReport implements Serializable {
    public static enum Level {
        Month,
        Day,
        Hour,
        Minute
    }

    public Level level;
    public Long beginDate;
    public Integer value;
    public Boolean success;
    public Boolean hadProblems;
    public ResultedReport() {

    }

    public ResultedReport(Level level, Long beginDate, Integer value, Boolean success, Boolean hadProblems) {
        this.level = level;
        this.beginDate = beginDate;
        this.value = value;
        this.success = success;
        this.hadProblems = hadProblems;
    }

    public Level getLevel() {
        return level;
    }

    public ResultedReport setLevel(Level level) {
        this.level = level;
        return this;
    }

    public Long getBeginDate() {
        return beginDate;
    }

    public ResultedReport setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public ResultedReport setValue(Integer value) {
        this.value = value;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ResultedReport setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Boolean getHadProblems() {
        return hadProblems;
    }

    public ResultedReport setHadProblems(Boolean hadProblems) {
        this.hadProblems = hadProblems;
        return this;
    }

    public void prePromote() {
        switch (level) {
            case Minute -> {
                Long rounded = (long) (Math.round(Double.valueOf(beginDate)/1000d/60d)*60d*1000);

            }
        }
    }
}
