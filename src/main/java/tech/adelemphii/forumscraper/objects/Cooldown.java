package tech.adelemphii.forumscraper.objects;

import net.dv8tion.jda.api.entities.Member;
import tech.adelemphii.forumscraper.utility.ScrapeUtility;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cooldown {

    private final Member member;
    private int cooldown = 10;

    private Runnable cooldownRunnable;

    public Cooldown(Member member) {
        this.member = member;
        startCooldown();
    }

    public void startCooldown() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        cooldownRunnable = () -> {
            if(cooldown == 0) {
                executor.shutdown();
            }
            cooldown--;
        };
        executor.scheduleAtFixedRate(cooldownRunnable, 0, 1, TimeUnit.SECONDS);
    }

    public Member getMember() {
        return member;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Runnable getCooldownRunnable() {
        return cooldownRunnable;
    }
}
