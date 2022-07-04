package tech.adelemphii.forumscraper.discord.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.forumscraper.discord.DiscordBot;
import tech.adelemphii.forumscraper.discord.commands.BaseCommand;
import tech.adelemphii.forumscraper.utility.data.Configuration;

public class MessageListener implements EventListener {

    private final DiscordBot discordBot;
    public MessageListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent messageReceivedEvent) {
            String prefix = Configuration.getInstance().getCommandPrefix();
            Message message = messageReceivedEvent.getMessage();

            if(message.getContentRaw().startsWith(prefix)) {
                String[] split = message.getContentRaw().split(" ");
                if(split.length == 0) {
                    return;
                }
                String command = split[0];
                command = command.replace(prefix, "");

                BaseCommand baseCommand = discordBot.commands.get(command);

                if(baseCommand != null) {
                    baseCommand.execute(messageReceivedEvent);
                }
            }
        }
    }
}
