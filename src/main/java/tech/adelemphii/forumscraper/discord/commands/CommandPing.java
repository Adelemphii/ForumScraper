package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.forumscraper.utility.GeneralUtility;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandPing implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        String name = "lotc";

        Map<String, EmbedBuilder> embed = GeneralUtility.pingServer("mc.lotc.co", name);
        Optional<String> base64Opt = embed.keySet().stream().findFirst();
        String base64 = "";
        if(base64Opt.isPresent()) {
            base64 = base64Opt.get();
        }

        EmbedBuilder serverEmbed = embed.get(base64);
        EmbedBuilder websiteEmbed = GeneralUtility.pingWebsite("https://www.lorcofthecraft.net/forums/");

        if(!base64.isEmpty()) {
            File file = GeneralUtility.getFileFromCache(name) != null
                    ? GeneralUtility.getFileFromCache(name) : GeneralUtility.decodeToFile(base64, name);
            if(file != null && file.exists()) {
                event.getChannel().sendFile(file, name + ".png").setEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                        .queue();
            }
        } else {
            event.getChannel().sendMessageEmbeds(List.of(serverEmbed.setThumbnail(null).build(), websiteEmbed.build())).queue();
        }

    }

    @Override
    public boolean requireAdmin() {
        return false;
    }

    @Override
    public String name() {
        return "ping";
    }

    @Override
    public List<String> subCommands() {
        return null;
    }
}
