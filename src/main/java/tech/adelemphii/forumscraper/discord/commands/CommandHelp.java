package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.forumscraper.ForumScraper;
import tech.adelemphii.forumscraper.discord.DiscordBot;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class CommandHelp implements BaseCommand {

    private Server server;

    @Override
    public void execute(MessageReceivedEvent event) {
        server = ServerStorageUtility.getServer(event.getGuild().getIdLong());

        event.getChannel().sendMessageEmbeds(getHelpEmbed()).queue();
    }

    @Override
    public boolean requireAdmin() {
        return false;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public List<String> subCommands() {
        return null;
    }

    private MessageEmbed getHelpEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        String commandPrefix = server.getCommandPrefix();

        builder.setTitle("__Bot Commands__");

        Map<String, BaseCommand> commands = ForumScraper.getDiscordBot().getCommands();

        StringBuilder sb = new StringBuilder();
        for(BaseCommand baseCommand : commands.values()) {
            sb.append("**").append(commandPrefix).append(baseCommand.name()).append("**");

            List<String> subCommands = baseCommand.subCommands();

            if(subCommands != null && !subCommands.isEmpty()) {
                for(String string : subCommands) {
                    sb.append("\n**").append(commandPrefix).append(baseCommand.name()).append(" ").append(string).append("**").append("\n");
                }
                continue;
            }
            sb.append("\n");
        }

        builder.setDescription(sb);

        builder.setColor(Color.BLACK);

        return builder.build();
    }
}
