package tech.adelemphii.forumscraper.discord.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class ReadyListener implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.println("API is ready!");
        }

        if(event instanceof GuildReadyEvent guildReadyEvent) {
            // make a runnable which runs every 5 minutes, and when it runs it'll scrape the information and update its message in
        }
    }
}
