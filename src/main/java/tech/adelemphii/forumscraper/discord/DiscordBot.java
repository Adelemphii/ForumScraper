package tech.adelemphii.forumscraper.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tech.adelemphii.forumscraper.discord.commands.BaseCommand;
import tech.adelemphii.forumscraper.discord.commands.CommandChannel;
import tech.adelemphii.forumscraper.discord.commands.CommandPing;
import tech.adelemphii.forumscraper.discord.events.MessageListener;
import tech.adelemphii.forumscraper.discord.events.ReadyListener;
import tech.adelemphii.forumscraper.utility.data.Configuration;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class DiscordBot {

    private JDA api;
    public Map<String, BaseCommand> commands = new HashMap<>();

    public DiscordBot(Configuration configuration) {

        if(login(configuration.getDiscordBotToken())) {
            System.out.println("Successfully logged in.");
        } else {
            System.out.println("An error has occurred while attempting to log in.");
            return;
        }
        registerEvents();
        registerCommands();
        api.getPresence().setActivity(Activity.playing("ForumScraper by Adelemphii"));
    }

    public void stop(boolean now) {
        if(now) {
            api.shutdownNow();
        } else {
            api.shutdown();
        }
    }

    private void registerEvents() {
        api.addEventListener(new ReadyListener());
        api.addEventListener(new MessageListener(this));
    }

    private void registerCommands() {
        commands.put("ping", new CommandPing());
        commands.put("channel", new CommandChannel());
    }

    public boolean login(String token) {
        try {
            api = JDABuilder.createDefault(token).build();
            return true;
        } catch(LoginException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JDA getApi() {
        return api;
    }
}
