package tech.adelemphii.forumscraper.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class CommandCredits implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(getCreditsEmbed()).queue();
    }

    @Override
    public boolean requireAdmin() {
        return false;
    }

    @Override
    public String name() {
        return "credits";
    }

    @Override
    public List<String> subCommands() {
        return null;
    }

    private MessageEmbed getCreditsEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("__Bot Credits & Socials__");

        builder.addField("*Created By*", "The bot was created by <@844957548134006806>", false);
        builder.addField("Portfolio", "Find Adelemphii's Portfolio @ https://www.adelemphii.tech/", false);
        builder.addField("Twitch", "Follow my Twitch @ https://www.twitch.tv/Adelemphii/", false);
        builder.addField("Requests/Suggetions", "Please send all feedback to: https://forms.gle/JXmJewmbLru8u8Mc7", false);

        builder.setColor(Color.PINK);

        return builder.build();
    }
}
