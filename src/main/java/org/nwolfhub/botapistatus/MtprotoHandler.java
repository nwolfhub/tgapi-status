package org.nwolfhub.botapistatus;

import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import org.nwolfhub.botapistatus.monitor.Report;
import org.nwolfhub.botapistatus.monitor.ReportWatcher;
import org.nwolfhub.easycli.model.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
public class MtprotoHandler {
    private SimpleTelegramClient client = null;
    private final Thread watcherThread;
    private Boolean working;

    public MtprotoHandler(SimpleTelegramClientBuilder clientBuilder, AuthenticationSupplier supplier) {
        working = true;
        watcherThread = new Thread(this::getMeWatcher);
        this.client=clientBuilder.build(supplier);
        watcherThread.start();
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
