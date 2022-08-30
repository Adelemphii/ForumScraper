package tech.adelemphii.forumscraper.discord.events;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.forumscraper.discord.DiscordBot;
import tech.adelemphii.forumscraper.discord.commands.BaseCommand;
import tech.adelemphii.forumscraper.objects.Cooldown;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.utility.GeneralUtility;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MessageListener implements EventListener {

    private final ArrayList<Cooldown> cooldowns = new ArrayList<>();

    private final DiscordBot discordBot;
    public MessageListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof MessageReceivedEvent event) {
            if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
                return;
            }

            if(!event.isFromGuild() || event.isWebhookMessage()
                    || event.getChannel().getType() != ChannelType.TEXT) {
                event.getMessage().reply("I can only accept commands in a server.").queue();
                return;
            }

            Guild guild = event.getGuild();
            Server server = ServerStorageUtility.getServer(guild.getIdLong());

            if(server == null) {
                System.out.println("Server: " + guild.getIdLong() + " does not have a saved config, generating a new one.");
                server = new Server(guild.getIdLong(), null, null, null, null, null, null);
                ServerStorageUtility.addServer(server);
            }

            if(server.getCommandsChannelID() != 0) {
                if(event.getChannel().getIdLong() != server.getCommandsChannelID()) {
                    return;
                }
            }

            String prefix = server.getCommandPrefix();
            Message message = event.getMessage();

            if(message.getContentRaw().startsWith(prefix)) {
                String[] split = message.getContentRaw().split(" ");
                if(split.length == 0) {
                    return;
                }
                String command = split[0];
                command = command.replace(prefix, "");

                BaseCommand baseCommand = discordBot.getCommands().get(command);

                Member member = event.getMember();

                ArrayList<Cooldown> toRemove = new ArrayList<>();
                if(!cooldowns.isEmpty()) {
                    for (Cooldown cooldown : cooldowns) {
                        if(cooldown.getMember().equals(member) && !GeneralUtility.isAdmin(guild, member, server)) {
                            if(cooldown.getCooldown() <= 0) {
                                toRemove.add(cooldown);
                            } else {
                                message.reply("You are on command cooldown for " + cooldown.getCooldown() + " seconds")
                                        .queue(msg -> msg.delete().queueAfter(cooldown.getCooldown(), TimeUnit.SECONDS));
                                return;
                            }
                        }
                    }
                    if(!toRemove.isEmpty()) {
                        cooldowns.removeAll(toRemove);
                    }
                }

                if(baseCommand != null) {
                    if(baseCommand.requireAdmin()) {
                        assert member != null;
                        if(!GeneralUtility.isAdmin(guild, member, server)) {
                            event.getMessage().reply("Only admins can use this command.")
                                    .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                            return;
                        }
                    }
                    baseCommand.execute(event);
                    cooldowns.add(new Cooldown(member));
                }
            }
        }
    }
}
