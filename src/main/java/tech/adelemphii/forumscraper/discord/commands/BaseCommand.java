package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface BaseCommand {

    void execute(MessageReceivedEvent event);
}
