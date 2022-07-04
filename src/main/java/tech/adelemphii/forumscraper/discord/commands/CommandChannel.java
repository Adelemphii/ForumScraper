package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.objects.TopicType;
import tech.adelemphii.forumscraper.utility.ScrapeUtility;
import tech.adelemphii.forumscraper.utility.data.Configuration;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

public class CommandChannel implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = ServerStorageUtility.getServer(guild.getIdLong());

        if(server == null) {
            System.out.println("Server: " + guild.getIdLong() + " does not have a saved config, generating a new one.");
            server = new Server(guild.getIdLong(), null, null, null, null, null, null);
        }

        String[] args = event.getMessage().getContentRaw()
                .replace(Configuration.getInstance().getCommandPrefix() + "channel ", "").split(" ");

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
        }
    }

    private void saveConfig(Message message) {
        ServerStorageUtility.saveServers();
        message.reply(":D").queue();
    }

    private void update(Guild guild, Message message) {
        String popularTopicError = ScrapeUtility.sendPopularTopics(guild);
        String latestTopicError = ScrapeUtility.sendLatestTopics(guild);
        String statusUpdateError = ScrapeUtility.sendStatusUpdates(guild);
        if(popularTopicError != null) {
            message.reply("Error: " + popularTopicError).queue();
        }

        if(latestTopicError != null) {
            message.reply("Error: " + latestTopicError).queue();
        }

        if(statusUpdateError != null) {
            message.reply("Error: " + statusUpdateError).queue();
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
            default -> {
                return false;
            }
        }
    }
}
