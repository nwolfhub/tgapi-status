package org.nwolfhub.botapistatus.monitor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReportWatcher {
    public static List<Report> mtprotoReports = new ArrayList<>();
    public static List<Report> botapiReports = new ArrayList<>();

    public static Normalized botNormalized = new Normalized();
    public static Normalized mtNormalized = new Normalized();
}
