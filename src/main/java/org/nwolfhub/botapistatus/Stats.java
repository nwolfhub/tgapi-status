package org.nwolfhub.botapistatus;

import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/")
public class Stats extends VerticalLayout {
    public Stats() {
        Span name = new Span("Telegram API status");
        VerticalLayout layout2 = new VerticalLayout();
        layout2.add(name);
        layout2.setAlignItems(Alignment.CENTER);
        add(layout2);
        add(new Divider());
        Chart chart = new Chart(ChartType.BULLET);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Telegram status");
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
