package org.nwolfhub.botapistatus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.nwolfhub.botapistatus.monitor.Report;
import org.nwolfhub.botapistatus.monitor.ReportWatcher;

import java.util.stream.Collectors;

@Route("/")
public class Stats extends VerticalLayout {
    public Stats() {
        Span name = new Span("Telegram API status");
        VerticalLayout layout2 = new VerticalLayout();
        layout2.add(name);
        layout2.setAlignItems(Alignment.CENTER);
        add(layout2);
        add(new Divider());
        Chart chart = new Chart(ChartType.TIMELINE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Telegram status");
        YAxis yAxis = new YAxis();
        yAxis.setCategories("Response time");
        conf.addyAxis(yAxis);
        ListSeries series = new ListSeries("Telegram api");
        ListSeries series1 = new ListSeries("MTProto api");
        System.out.println(ReportWatcher.botapiReports);
        series.setData(ReportWatcher.botapiReports.stream().map(Report::getMs).collect(Collectors.toList()));
        series1.setData(ReportWatcher.mtprotoReports.stream().map(Report::getMs).collect(Collectors.toList()));
        conf.addSeries(series);
        conf.addSeries(series1);
        add(chart);

    }
    public static class Divider extends Span {
        public Divider() {
            getStyle().set("background-color", "black");
            getStyle().set("flex", "0 0 1px");
            getStyle().set("margin-top", "-10px");
            getStyle().set("align-self", "stretch");
        }
    }
}
