package org.nwolfhub.botapistatus;

import com.pengrad.telegrambot.TelegramBot;
import it.tdlight.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Value("${cfg.mtproto.token}")
    private String mtprotoToken;
    @Value("${cfg.bot.token}")
    private String botToken;
    @Value("${cfg.mtproto.api.id}")
    private Integer api_id;
    @Value("${cfg.mtproto.hash}")
    private String hash;
    @Bean
    public SimpleTelegramClient getClient() {
        try (SimpleTelegramClientFactory factory = new SimpleTelegramClientFactory()) {
            APIToken token = new APIToken(api_id, hash);
            TDLibSettings settings = TDLibSettings.create(new APIToken(api_id, hash));
            Path sessionPath = Paths.get("mtproto-session");
            settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
            settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
            SimpleTelegramClientBuilder builder = factory.builder(settings);
            SimpleAuthenticationSupplier<?> data = AuthenticationSupplier.bot(mtprotoToken);
            return builder.build(data);
        }
    }

    @Bean
    public TelegramBot getBot() {
        return new TelegramBot(botToken);
    }
}
