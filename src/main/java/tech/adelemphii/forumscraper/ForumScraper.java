package tech.adelemphii.forumscraper;

import tech.adelemphii.forumscraper.discord.DiscordBot;
import tech.adelemphii.forumscraper.utility.data.Configuration;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.io.File;
import java.net.URISyntaxException;

public class ForumScraper {

    private static Configuration configuration;
    private static DiscordBot discordBot;

    public static void main(String[] args) {

        configuration = Configuration.getInstance();

        File file;
        try {
            file = new File(ForumScraper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch(URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }



        file = new File(file.getPath() + "/files/config.yml");
        configuration.loadConfiguration(file);

        ServerStorageUtility.loadServers();

        discordBot = new DiscordBot(configuration);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            discordBot.stop(false);
            ServerStorageUtility.saveServers();

            System.out.println("Shutting down...");
        }, "Shutdown-thread"));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }
}
