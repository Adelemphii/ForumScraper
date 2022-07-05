package tech.adelemphii.forumscraper.utility;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import tech.adelemphii.forumscraper.ForumScraper;
import tech.adelemphii.forumscraper.objects.Server;

public class GeneralUtility {

    public static boolean isAdmin(Guild guild, Member member, Server server) {
        Role role = guild.getRoleById(server.getAdminRoleID());
        return member.getRoles().contains(role) || member.getPermissions().contains(Permission.ADMINISTRATOR);
    }

    public static Message getUpdateMessage(Server server, long channel, long messageID) {
        JDA jda = ForumScraper.getDiscordBot().getApi();
        if(jda.isUnavailable(server.getServerID())) {
            return null;
        }

        Guild guild = jda.getGuildById(server.getServerID());
        if(guild == null) {
            return null;
        }

        TextChannel textChannel = guild.getTextChannelById(channel);
        if(textChannel == null) {
            return null;
        }

        return textChannel.retrieveMessageById(messageID).complete();
    }
}
