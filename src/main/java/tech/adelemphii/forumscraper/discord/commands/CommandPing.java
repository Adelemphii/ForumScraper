package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPing implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().reply("Pong!").queue();
    }
}
