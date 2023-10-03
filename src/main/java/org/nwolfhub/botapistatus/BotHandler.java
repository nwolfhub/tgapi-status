package org.nwolfhub.botapistatus;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.nwolfhub.botapistatus.monitor.Report;
import org.nwolfhub.botapistatus.monitor.ReportWatcher;
import org.nwolfhub.easycli.model.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotHandler {
    private TelegramBot bot;
    private Boolean working;
    private Thread workingThread;

    public BotHandler(TelegramBot bot) {
        this.bot = bot;
        working = true;
        workingThread = new Thread(this::listen);
        workingThread.start();
    }
    private void listen() {
        int offsetId = 0;
        List<Update> updateList = new ArrayList<>();
        while (working) {
            GetUpdates request = new GetUpdates().limit(100).offset(offsetId).timeout(10);
            Long sentGet = new Date().getTime();
            try {
                GetUpdatesResponse response = bot.execute(request);
                updateList.clear();
                updateList.addAll(response.updates());
                if(!updateList.isEmpty()) {
                    offsetId = updateList.get(updateList.size() - 1).updateId() + 1;
                    for (Update update : updateList) {
                        new Thread( () -> {
                            if (update.message() != null && update.message().text()!=null) {
                                Long sent = new Date().getTime();
                                bot.execute(new SendMessage(update.message().chat().id(), update.message().text()), new Callback<SendMessage, SendResponse>() {
                                    @Override
                                    public void onResponse(SendMessage request, SendResponse response) {
                                        Long result = new Date().getTime();
                                        ReportWatcher.botNormalized.addValue(result-sent);
                                        ReportWatcher.botapiReports.add(new Report(new Date().getTime(), Math.toIntExact(result - sent), true));
                                    }

                                    @Override
                                    public void onFailure(SendMessage request, IOException e) {
                                        Long result = new Date().getTime();
                                        //normalized not counted here as the request was not successful
                                        ReportWatcher.botapiReports.add(new Report(new Date().getTime(), Math.toIntExact(result - sent), false));
                                    }
                                });
                            }
                        }).start();
                    }
                } else {
                    new Thread(() -> {
                        Long sent = new Date().getTime();
                        GetMeResponse getMe = bot.execute(new GetMe());
                        if(getMe.isOk()) {
                            Long result = new Date().getTime();
                            ReportWatcher.botNormalized.addValue(result-sent);
                            ReportWatcher.botapiReports.add(new Report(new Date().getTime(), Math.toIntExact(result - sent), true));
                        } else {
                            Long result = new Date().getTime();
                            //normalized not counted here as the request was not successful
                            ReportWatcher.botapiReports.add(new Report(new Date().getTime(), Math.toIntExact(result - sent), false));
                        }
                    }).start();
                }
            } catch (Exception e) {
                ReportWatcher.botapiReports.add(new Report(new Date().getTime(), Math.toIntExact(new Date().getTime()-sentGet), false));
            }
        }
    }

    public void close() {
        working = false;
        workingThread.interrupt();
    }
}
