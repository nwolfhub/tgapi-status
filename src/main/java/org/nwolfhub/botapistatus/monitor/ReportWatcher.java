package org.nwolfhub.botapistatus.monitor;

import org.nwolfhub.botapistatus.BotApiStatusApplication;
import org.nwolfhub.easycli.model.Level;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReportWatcher {
    public static List<Report> mtprotoReports = new ArrayList<>();
    public static List<Report> botapiReports = new ArrayList<>();

    public static Normalized botNormalized = new Normalized();
    public static Normalized mtNormalized = new Normalized();

    public static List<ResultedReport> minuteBotReports = new ArrayList<>();
    public static List<ResultedReport> minuteMtReports = new ArrayList<>();


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

    private void exportReportToFile(ResultedReport report, Report.Type type) {
        File baseDir = type==Report.Type.botapi?new File("reports/botapi/"):new File("reports/mtproto");
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
                Thread.sleep(60000);
                
            } catch (InterruptedException e) {
                BotApiStatusApplication.cli.printAtLevel(Level.Info, "Shutting down promoter");
            }
        }
    }
    
    
    private static ResultedReport createResultedReport(List<Report> reports, ResultedReport.Level level) {
        HashMap<Long, Integer> beginDates = new HashMap<>();
        AtomicReference<Integer> value = new AtomicReference<>(0);
        AtomicReference<Integer> successAmount = new AtomicReference<>(0);
        reports.forEach(e -> {
            Long floored = (long) (Math.floor((double) e.getDate()/calculateFloorValue(level))*calculateFloorValue(level));
            if(beginDates.containsKey(floored)) beginDates.put(floored, beginDates.get(floored)+1); else beginDates.put(floored, 1);
            value.updateAndGet(v -> v + e.getMs());
            if(e.getSuccess()) successAmount.getAndSet(successAmount.get() + 1);
        });
        Integer max = beginDates.keySet().stream().map(beginDates::get).max(Comparator.naturalOrder()).get();
        Long beginDate = (Long) IntStream.range(0, beginDates.size()).filter(i -> beginDates.get(beginDates.keySet().toArray()[i]).equals(max)).mapToObj(i->beginDates.keySet().toArray()[i]).toList().get(0);
        value.set(value.get()/reports.size());
        boolean success = Double.valueOf(successAmount.get()) / (double) reports.size() > 0.9d;
        boolean hadProblems = successAmount.get()<reports.size();
        return new ResultedReport(ResultedReport.Level.values()[level.ordinal()+1], beginDate, value.get(), success, hadProblems);
    }

    private static Long calculateFloorValue(ResultedReport.Level level) {
        switch (level) {
            case Minute -> {
                return 1000L*60L;
            } case Hour -> {
                return 1000L*60L*60L;
            }
            case Day -> {
                return 1000L*60L*60L*24;
            } case Month -> {
                return 1000L*60L*60L*30L;
            }
        }
        return 1L;
    }
}
