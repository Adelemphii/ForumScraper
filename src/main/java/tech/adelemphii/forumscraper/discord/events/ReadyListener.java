package tech.adelemphii.forumscraper.discord.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.forumscraper.discord.DiscordBot;
import tech.adelemphii.forumscraper.utility.ScrapeUtility;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadyListener implements EventListener {

    private final DiscordBot discordBot;

    public ReadyListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.println("API is ready!");
        }

        if(event instanceof GuildReadyEvent guildReadyEvent) {
            Runnable statusRunnable = () -> {
                ScrapeUtility.sendStatusUpdates(guildReadyEvent.getGuild());
                ScrapeUtility.sendPopularTopics(guildReadyEvent.getGuild());
                ScrapeUtility.sendLatestTopics(guildReadyEvent.getGuild());
                ScrapeUtility.sendPingUpdate(guildReadyEvent.getGuild());
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(statusRunnable, 0, 10, TimeUnit.MINUTES);

            discordBot.addUpdateRunnable(guildReadyEvent.getGuild().getIdLong(), statusRunnable);
        }

    }
}
