package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.utility.ScrapeUtility;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.util.Arrays;
import java.util.List;

public class CommandChannel implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = ServerStorageUtility.getServer(guild.getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getCommandPrefix() + "channel ", "").split(" ");

        if(args[0].equalsIgnoreCase("set")) {
            if(Long.getLong(args[2]) != null) {
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4E")).queue();
            }
            if(set(args, server)) {
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
                ServerStorageUtility.addServer(server);
            } else {
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4E")).queue();
            }
        } else if(args[0].equalsIgnoreCase("saveconfig")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
            saveConfig(event.getMessage());
        } else if(args[0].equalsIgnoreCase("update")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
            update(guild, event.getMessage());
        } else if(args[0].equalsIgnoreCase("help")) {
            help(server, event.getMessage());
        }
    }

    private void help(Server server, Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Channel Command Args");
        builder.setDescription(server.getCommandPrefix() + "channel <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, saveconfig, update, help", false);
        builder.addField("Argument 2", "Valid types: popular_topics, latest_topics, status_updates, ping_updates", false);
        builder.addField("Argument 3", "Channel ID, such as 819699195681832991", false);

        message.reply("Examples: " + server.getCommandPrefix() + "channel set popular_topics 910726981610512415 or " +
                server.getCommandPrefix() + "channel update").setEmbeds(builder.build()).queue();
    }

    private void saveConfig(Message message) {
        ServerStorageUtility.saveServers();
        message.reply(":D").queue();
    }

    private void update(Guild guild, Message message) {
        String popularTopicError = ScrapeUtility.sendPopularTopics(guild);
        String latestTopicError = ScrapeUtility.sendLatestTopics(guild);
        String statusUpdateError = ScrapeUtility.sendStatusUpdates(guild);
        String pingUpdateError = ScrapeUtility.sendPingUpdate(guild);
        if(popularTopicError != null) {
            message.reply("Error: " + popularTopicError).queue();
        }

        if(latestTopicError != null) {
            message.reply("Error: " + latestTopicError).queue();
        }

        if(statusUpdateError != null) {
            message.reply("Error: " + statusUpdateError).queue();
        }

        if(pingUpdateError != null) {
            message.reply("Error: " + pingUpdateError).queue();
        }
    }

    private boolean set(String[] args, Server server) {
        switch (args[1].toUpperCase()) {
            case "POPULAR_TOPICS" -> {
                server.setPopularTopicsChannel(Long.parseLong(args[2]));
                return true;
            }
            case "LATEST_TOPICS" -> {
                server.setLatestTopicsChannel(Long.parseLong(args[2]));
                return true;
            }
            case "STATUS_UPDATES" -> {
                server.setStatusUpdatesChannel(Long.parseLong(args[2]));
                return true;
            }
            case "PING_UPDATES" -> {
                server.setPingUpdateChannel(Long.parseLong(args[2]));
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "channel";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "saveconfig", "update", "help");
    }
}
