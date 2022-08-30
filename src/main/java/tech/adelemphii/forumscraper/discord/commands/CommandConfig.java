package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.utility.GeneralUtility;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.util.Arrays;
import java.util.List;

public class CommandConfig implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = ServerStorageUtility.getServer(guild.getIdLong());
        Message message = event.getMessage();

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getCommandPrefix() + "config ", "").split(" ");

        if(args[0].equalsIgnoreCase("display")) {
            MessageEmbed embed = display(server);
            event.getChannel().sendMessageEmbeds(embed).queue();
            return;
        }
        if(args[0].equalsIgnoreCase("help") || args.length < 3) {
            MessageEmbed embed = help(server);
            message.reply("Examples: ").setEmbeds(embed).queue();

        } else if(args[0].equalsIgnoreCase("set")) {
            String type = args[1];
            String value = args[2];
            if(determineType(type, value, server)) {
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
            } else {
                event.getMessage().reply("config " + type + " " + value + " is not proper usage.")
                        .setEmbeds(help(server)).queue();
            }
        }
    }

    private boolean determineType(String type, String value, Server server) {
        switch(type.toUpperCase()) {
            case "PREFIX":
                if(value.length() != 1) {
                    return false;
                }

                server.setCommandPrefix(value);
                ServerStorageUtility.addServer(server);
                return true;
            case "ADMIN_ROLE":
                try {
                    long roleID = Long.parseLong(value);
                    server.setAdminRoleID(roleID);
                    ServerStorageUtility.addServer(server);
                    return true;
                } catch(NumberFormatException e) {
                    return false;
                }
            case "COMMANDS_CHANNEL":
                try {
                    long commandChannelID = Long.parseLong(value);
                    server.setCommandsChannelID(commandChannelID);
                    ServerStorageUtility.addServer(server);
                    return true;
                } catch(NumberFormatException e) {
                    return false;
                }
            default:
                return false;
        }
    }

    private MessageEmbed display(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Display");

        builder.addField("Prefix", "'" + server.getCommandPrefix() + "'", true);
        builder.addField("Admin Role",  server.getAdminRoleID() + "", true);
        builder.addField("Commands Channel", "<#" + server.getCommandsChannelID() + ">", false);

        Long popTop = server.getPopularTopicsChannel();
        Long latTop = server.getLatestTopicsChannel();
        Long statUp = server.getStatusUpdatesChannel();
        Long pingUpdate = server.getPingUpdateChannel();

        builder.addField("Popular Topics Channel", "<#" + popTop + ">", true);
        builder.addField("Latest Topics Channel",  "<#" + latTop + ">", true);
        builder.addField("Status Updates Channel", "<#" + statUp + ">", true);
        builder.addField("Ping Update Channel", "<#" + pingUpdate + ">", true);

        Message popTopMessage = GeneralUtility.getUpdateMessage(server, popTop, server.getPopularTopicMessage());
        if(popTopMessage != null) {
            builder.addField("Popular Topics Message", popTopMessage.getJumpUrl(), false);
        }
        Message latTopMessage = GeneralUtility.getUpdateMessage(server, latTop, server.getLatestTopicsMessage());
        if(latTopMessage != null) {
            builder.addField("Latest Topics Message", latTopMessage.getJumpUrl(), false);
        }
        Message statUpMessage = GeneralUtility.getUpdateMessage(server, statUp, server.getStatusUpdatesMessage());
        if(statUpMessage != null) {
            builder.addField("Status Updates Message", statUpMessage.getJumpUrl(), false);
        }
        Message pingUpdateMessage = GeneralUtility.getUpdateMessage(server, pingUpdate, server.getPingUpdateMessage());
        if(pingUpdateMessage != null) {
            builder.addField("Ping Updates Message", pingUpdateMessage.getJumpUrl(), false);
        }

        return builder.build();
    }

    private MessageEmbed help(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Commands");

        builder.setDescription("*Note: The prefix can only be 1 character long*");

        builder.addField("Prefix", server.getCommandPrefix() + "config set prefix ?", true);
        builder.addField("Admin Role", server.getCommandPrefix() + "config set admin_role 866292160495353876", true);
        builder.addField("Commands Channel", server.getCommandPrefix() + "config set commands_channel 993383846135476366", true);
        return builder.build();
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "config";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("display", "help", "set");
    }
}
