package tech.adelemphii.forumscraper.discord.events;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.forumscraper.objects.Topic;
import tech.adelemphii.forumscraper.utility.ScrapeUtility;

import java.util.Map;

public class ReadyListener implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.println("API is ready!");
            Map<Topic, MessageEmbed> topics = ScrapeUtility.scrapeTopics("https://www.lordofthecraft.net/forums/");

            if(topics == null) {
                System.out.println("Something went wrong, is the site down?");
                return;
            }

            event.getJDA().getGuildById(844958913178370129L).getTextChannelById(993043490122506301L).sendMessageEmbeds(topics.values()).queue();
        }
    }
}
