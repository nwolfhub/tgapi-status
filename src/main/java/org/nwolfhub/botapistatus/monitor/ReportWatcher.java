package org.nwolfhub.botapistatus.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportWatcher {
    public static List<Report> mtprotoReports = new ArrayList<>();
    public static List<Report> botapiReports = new ArrayList<>();

    public static Normalized botNormalized = new Normalized();
    public static Normalized mtNormalized = new Normalized();

    public static List<ResultedReport> minuteReports;

    private void levelDates() {
        walkOnReports(mtprotoReports, botapiReports);
        walkOnReports(botapiReports, mtprotoReports);
    }

    private void walkOnReports(List<Report> botapiReports, List<Report> mtprotoReports) {
        for(Report botReport: botapiReports) {
            if(mtprotoReports.stream().map(Report::getDate).noneMatch(e -> Objects.equals(e, botReport.getDate()))) {
                Report closest = new Report(0L, 0, false);
                for(Report mtReport: mtprotoReports) {
                    if(Math.abs(mtReport.date-closest.date) < closest.date) {
                        closest = new Report(botReport.date, mtReport.ms, mtReport.success);
                    }
                }
                mtprotoReports.add(closest);
            }
        }
    }

    private void levelerThread() {
        while (true) {
            try {
                levelDates();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Leveler interrupted:");
                e.printStackTrace();
                break;
            }
        }
    }

    private void promoterThread() {
        while (true) {
            try {

            }
        }
    }
}
