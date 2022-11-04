package tech.adelemphii.forumscraper.utility;

import me.nurio.minecraft.pinger.MinecraftServerPinger;
import me.nurio.minecraft.pinger.beans.MinecraftServerStatus;
import me.nurio.minecraft.pinger.beans.OfflineMinecraftServerStatus;
import me.nurio.minecraft.pinger.beans.Players;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.io.FileUtils;
import tech.adelemphii.forumscraper.ForumScraper;
import tech.adelemphii.forumscraper.objects.Server;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class GeneralUtility {

    public static boolean isAdmin(Guild guild, Member member, Server server) {
        Role role = guild.getRoleById(server.getAdminRoleID());
        return member.getRoles().contains(role) || member.getPermissions().contains(Permission.ADMINISTRATOR);
    }

    public static Message getUpdateMessage(Server server, Long channel, Long messageID) {
        if(channel == null || messageID == null) {
            return null;
        }

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

    public static Map<String, EmbedBuilder> pingServer(String address, String name) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        MinecraftServerStatus server = MinecraftServerPinger.ping(address);

        embedBuilder.setTitle("Server Status");

        if(server.isOffline()) {
            embedBuilder.addField("Server IP", address, false);
            embedBuilder.addField("Server Status", "Offline.", false);
            embedBuilder.setColor(Color.RED);

            // You can get the server connection exception like that:
            OfflineMinecraftServerStatus offlineServer = (OfflineMinecraftServerStatus) server;
            offlineServer.getException().printStackTrace();
            return Map.of("", embedBuilder);
        }

        String motd = server.getMotd();
        Players players = server.getPlayers();
        String base64Favicon = server.getFavicon();

        embedBuilder.addField("Server IP", address, false);
        embedBuilder.addField("MoTD", motd, false);
        embedBuilder.addField("Players", players.getOnline() + "/" + players.getMax(), false);

        embedBuilder.setThumbnail("attachment://" + name + ".png");

        embedBuilder.setColor(Color.GREEN);
        return Map.of(base64Favicon, embedBuilder);
    }

    public static EmbedBuilder pingWebsite(String url) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Website Status");

        boolean online = ScrapeUtility.upCheck(url);

        String text = online ? "The Website is **ONLINE!**" : "The website is **OFFLINE!**";
        embedBuilder.setDescription(text);

        Color color = online ? Color.GREEN : Color.RED;
        embedBuilder.setColor(color);
        return embedBuilder;
    }

    public static File saveImageToCache(byte[] decodedBytes, String name) {
        try {
            File output = new File(ForumScraper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            output = new File(output.getAbsolutePath() + "/files/cache/" + name + ".png");
            FileUtils.writeByteArrayToFile(output, decodedBytes);
            return output;
        } catch(URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFileFromCache(String fileName) {
        try {
            File output = new File(ForumScraper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            output = new File(output.getAbsolutePath() + "/files/cache/" + fileName + ".png");
            return output;
        } catch(URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File decodeToFile(String base64String, String name) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String.substring(base64String.indexOf(",") + 1)
                .getBytes(StandardCharsets.UTF_8));
        return saveImageToCache(decodedBytes, name);
    }
}
