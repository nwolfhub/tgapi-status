package org.nwolfhub.botapistatus;

import com.pengrad.telegrambot.TelegramBot;
import it.tdlight.client.*;
import org.nwolfhub.easycli.Defaults;
import org.nwolfhub.easycli.EasyCLI;
import org.nwolfhub.easycli.model.Level;
import org.nwolfhub.utils.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class BotApiStatusApplication {
	public static EasyCLI cli;

	public static void main(String[] args) {
		cli = new EasyCLI();
		cli.addTemplate(Defaults.loggingTemplate);
		cli.setLevel(Level.Info);
		cli.printAtLevel(Level.Info, "Initialization began");
		String defaultConfig = """
				mtproto_api_id=
				mtproto_hash=
				mtproto_token=
				bot_token=""";
		cli.printAtLevel(Level.Info, "Reading config");
		File configFile = new File("botstats.cfg");
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
				try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
					outputStream.write(defaultConfig.getBytes());
				}
				cli.printAtLevel(Level.Panic, "File " + configFile.getAbsolutePath() + " was created. Fill in the blanks!");
			} catch (IOException e) {
				cli.printAtLevel(Level.Panic, "Error while creating file:", e.getMessage());
			}
		}
		Configurator configurator = new Configurator(configFile);
		int api_id = Integer.parseInt(configurator.getSingleValue("mtproto_api_id"));
		String hash = configurator.getSingleValue("mtproto_hash");
		String mtprotoToken = configurator.getSingleValue("mtproto_token");
		String botToken = configurator.getSingleValue("bot_token");
		cli.printAtLevel(Level.Info, "Finished reading config");
		//System.setOut(null); //fuck tdlight
		new Thread(() -> {
			try (SimpleTelegramClientFactory factory = new SimpleTelegramClientFactory()) {
				cli.printAtLevel(Level.Info, "Creating mtproto bot instance");
				APIToken token = new APIToken(api_id, hash);
				TDLibSettings settings = TDLibSettings.create(new APIToken(api_id, hash));
				Path sessionPath = Paths.get("mtproto-session");
				settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
				settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
				SimpleTelegramClientBuilder builder = factory.builder(settings);
				SimpleAuthenticationSupplier<?> data = AuthenticationSupplier.bot(mtprotoToken);
				new MtprotoHandler(builder, data);
			} catch (Exception e) {
				cli.printAtLevel(Level.Panic, "Error while creating mtproto client:", e);
            }
		}).start();
		cli.printAtLevel(Level.Info, "Creating bot api instance");
		TelegramBot bot = new TelegramBot(botToken);
		new BotHandler(bot);
		SpringApplication.run(BotApiStatusApplication.class, args);
	}

}
