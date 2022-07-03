package tech.adelemphii.forumscraper.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tech.adelemphii.forumscraper.discord.events.ReadyListener;
import tech.adelemphii.forumscraper.utility.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscordBot {

    private JDA api;
    private ScheduledExecutorService statusRunnable;

    private List<String> activityMessages = List.of(
            "ForumScraper by Adelemphii",
            ":D",
            "Yay!"
    );

    public DiscordBot(Configuration configuration) {

        if(login(configuration.getDiscordBotToken())) {
            System.out.println("Successfully logged in.");
        } else {
            System.out.println("An error has occurred while attempting to log in.");
            return;
        }
        registerEvents();
        startActivityUpdateTask();
    }

    public void stop(boolean now) {
        if(now) {
            api.shutdownNow();
            statusRunnable.shutdownNow();
        } else {
            api.shutdown();
            statusRunnable.shutdown();
        }
    }


    private void startActivityUpdateTask() {
        AtomicInteger current = new AtomicInteger();
        Runnable statusRunnable = () -> {
            String message = activityMessages.get(current.get());
            current.getAndIncrement();

            api.getPresence().setActivity(Activity.playing(message));
            System.out.println("Activity is now... " + message);

        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(statusRunnable, 0, 20, TimeUnit.SECONDS);

        this.statusRunnable = executor;
    }

    private void registerEvents() {
        api.addEventListener(new ReadyListener());

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

    public List<String> getActivityMessages() {
        return activityMessages;
    }

    public void setActivityMessages(List<String> activityMessages) {
        this.activityMessages = activityMessages;
    }

    public void addActivityMessage(String string) {
        activityMessages.add(string);
    }
}
