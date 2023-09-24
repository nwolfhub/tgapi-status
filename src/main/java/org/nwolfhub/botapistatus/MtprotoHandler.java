package org.nwolfhub.botapistatus;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import org.nwolfhub.botapistatus.monitor.Report;
import org.nwolfhub.botapistatus.monitor.ReportWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MtprotoHandler {
    @Autowired
    private SimpleTelegramClient client;
    private Thread watcherThread;
    private Boolean working;

    public MtprotoHandler() {
        working = true;
        watcherThread = new Thread(this::getMeWatcher);
        watcherThread.start();
        client.addUpdateHandler(TdApi.UpdateNewMessage.class, updateNewMessage -> {
            new Thread(() -> {
            if(updateNewMessage.message!=null)
                {
                    TdApi.SendMessage message = new TdApi.SendMessage();
                    message.chatId = updateNewMessage.message.chatId;
                    message.inputMessageContent = new TdApi.InputMessageText(new TdApi.FormattedText("Pong", new TdApi.TextEntity[0]), true, true);
                    Long sent = new Date().getTime();
                    message.inputMessageContent = new TdApi.InputMessageText(new TdApi.FormattedText("", new TdApi.TextEntity[]{}), true, true);
                    client.send(message, result -> {
                        Long response = new Date().getTime();
                        if (!result.isError()) {
                            ReportWatcher.mtprotoReports.add(new Report(new Date().getTime(), Math.toIntExact(response - sent), true));
                        } else {
                            ReportWatcher.mtprotoReports.add(new Report(new Date().getTime(), Math.toIntExact(response - sent), false));
                        }
                    });
                }
            }).start();
        });
    }

    private void getMeWatcher() {
        while (working) {
            try {
                TdApi.GetMe getMe = new TdApi.GetMe();
                Long sent = new Date().getTime();
                client.send(getMe, result -> {
                    Long response = new Date().getTime();
                    if (!result.isError()) {
                        ReportWatcher.mtprotoReports.add(new Report(new Date().getTime(), Math.toIntExact(response - sent), true));
                    } else {
                        ReportWatcher.mtprotoReports.add(new Report(new Date().getTime(), Math.toIntExact(response - sent), false));
                    }
                });
                Thread.sleep(10000);
            } catch (Exception ignored) {}
        }
    }

    public void close() throws Exception {
        working = false;
        watcherThread.interrupt();
        client.close();
    }

}
