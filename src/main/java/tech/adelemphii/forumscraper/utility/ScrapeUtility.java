package tech.adelemphii.forumscraper.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.adelemphii.forumscraper.objects.Author;
import tech.adelemphii.forumscraper.objects.Server;
import tech.adelemphii.forumscraper.objects.Topic;
import tech.adelemphii.forumscraper.objects.TopicType;
import tech.adelemphii.forumscraper.utility.data.ServerStorageUtility;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScrapeUtility {

    public static MessageEmbed createTopicEmbed(Topic topic, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(topic.getTitle(), topic.getUrl());
        builder.setColor(color);
        builder.setAuthor(topic.getAuthor().getName(), topic.getAuthor().getUrl(), topic.getAuthor().getImage());

        TemporalAccessor accessor = Instant.ofEpochMilli(topic.getPostDate().getMillis());
        builder.setTimestamp(accessor);

        builder.addField("Comment Count", topic.getCommentCount() + "", true);

        builder.addField("Time Posted", TimeFormat.RELATIVE.format(topic.getPostDate().getMillis()), true);

        builder.setFooter(topic.getTopicType().getName());

        return builder.build();
    }

    public static MessageEmbed createStatusEmbed(Topic topic, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(topic.getAuthor().getName(), topic.getUrl());
        builder.setColor(color);

        builder.setDescription(topic.getTitle());

        TemporalAccessor accessor = Instant.ofEpochMilli(topic.getPostDate().getMillis());
        builder.setTimestamp(accessor);

        builder.addField("Reply Count", topic.getCommentCount() + "", true);
        builder.addField("Time Posted", TimeFormat.RELATIVE.format(topic.getPostDate().getMillis()), true);

        builder.setFooter(topic.getTopicType().getName());

        return builder.build();
    }

    private static String doChecks(Server server, Guild guild, ArrayList<Topic> topicList) {
        if(server == null) {
            return "SERVER IS NULL";
        }

        TextChannel channel = guild.getTextChannelById(server.getPopularTopicsChannel());
        if(channel == null) {
            return "POPULAR TOPICS CHANNEL NOT SELECTED";
        }

        if(topicList == null) {
            return "TOPICS NOT LOADED";
        }
        return null;
    }

    private static String doChecks(Server server, Guild guild) {
        if(server == null) {
            return "SERVER IS NULL";
        }

        TextChannel channel = guild.getTextChannelById(server.getPingUpdateChannel());
        if(channel == null) {
            return "POPULAR TOPICS CHANNEL NOT SELECTED";
        }
        return null;
    }

    private static ArrayList<Topic> filterTopics(ArrayList<Topic> topicList, TopicType topicType) {
        ArrayList<Topic> topicsToRemove = new ArrayList<>();
        assert topicList != null;
        for(Topic topic : topicList) {
            if(topic.getTopicType() != topicType) {
                topicsToRemove.add(topic);
            }
        }
        for(Topic topic : topicsToRemove) {
            topicList.remove(topic);
        }

        if(topicType == TopicType.POPULAR_TOPIC) {
            topicList.sort((t1, t2) -> t2.getCommentCount() - t1.getCommentCount());
        } else {
            topicList.sort((t1, t2) -> t2.getPostDate().compareTo(t1.getPostDate()));
        }
        return topicList;
    }

    private static ArrayList<MessageEmbed> filterEmbeds(ArrayList<Topic> topicList) {
        ArrayList<MessageEmbed> embedList = new ArrayList<>();
        for(Topic topic : topicList) {
            embedList.add(topic.getEmbed());
        }
        return embedList;
    }

    public static String sendPopularTopics(Guild guild) {
        Server server = ServerStorageUtility.getServer(guild.getIdLong());
        ArrayList<Topic> topicList = scrapeTopics("https://www.lordofthecraft.net/forums/");

        String checks = doChecks(server, guild, topicList);

        if(checks != null) {
            return checks;
        }

        TextChannel channel = guild.getTextChannelById(server.getPopularTopicsChannel());

        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.POPULAR_TOPIC);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        assert channel != null;
        if(server.getPopularTopicMessage() == null) {
            channel.sendMessageEmbeds(embedList).queue(message -> server.setPopularTopicMessage(message.getIdLong()));
            ServerStorageUtility.addServer(server);
        } else {
            channel.retrieveMessageById(server.getPopularTopicMessage())
                    .queue(topicMessage -> topicMessage.editMessageEmbeds(embedList).queue(),
                            new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                channel.sendMessageEmbeds(embedList).queue(message
                                        -> server.setPopularTopicMessage(message.getIdLong()));
                                ServerStorageUtility.addServer(server);
                            })
                    );
        }
        return null;
    }

    public static String sendLatestTopics(Guild guild) {
        Server server = ServerStorageUtility.getServer(guild.getIdLong());
        ArrayList<Topic> topicList = scrapeTopics("https://www.lordofthecraft.net/forums/");

        String checks = doChecks(server, guild, topicList);

        if(checks != null) {
            return checks;
        }

        TextChannel channel = guild.getTextChannelById(server.getLatestTopicsChannel());

        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.LATEST_TOPIC);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        assert channel != null;
        if(server.getPopularTopicMessage() == null) {
            channel.sendMessageEmbeds(embedList).queue(message -> server.setLatestTopicsMessage(message.getIdLong()));
            ServerStorageUtility.addServer(server);
        } else {
            channel.retrieveMessageById(server.getLatestTopicsMessage())
                    .queue(topicMessage -> topicMessage.editMessageEmbeds(embedList).queue(),
                            new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                channel.sendMessageEmbeds(embedList).queue(message
                                        -> server.setLatestTopicsMessage(message.getIdLong()));
                                ServerStorageUtility.addServer(server);
                            })
                    );
        }
        return null;
    }

    public static String sendPingUpdate(Guild guild) {
        Server server = ServerStorageUtility.getServer(guild.getIdLong());
        String checks = doChecks(server, guild);

        if(checks != null) {
            return checks;
        }

        String name = "lotc";

        Map<String, EmbedBuilder> embed = GeneralUtility.pingServer("mc.lotc.co", name);
        Optional<String> base64Opt = embed.keySet().stream().findFirst();
        String base64 = "";
        if(base64Opt.isPresent()) {
            base64 = base64Opt.get();
        }

        EmbedBuilder serverEmbed = embed.get(base64);
        EmbedBuilder websiteEmbed = GeneralUtility.pingWebsite("https://www.lordofthecraft.net/forums/");

        TextChannel channel = guild.getTextChannelById(server.getPingUpdateChannel());

        assert channel != null;
        if(server.getPingUpdateMessage() == null) {
            File file = GeneralUtility.getFileFromCache(name) != null
                    ? GeneralUtility.getFileFromCache(name) : GeneralUtility.decodeToFile(base64, name);
            if(file != null && file.exists()) {
                channel.sendFile(file, name + ".png").setEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                        .queue(message -> server.setPingUpdateMessage(message.getIdLong()));
            } else {
                channel.sendMessageEmbeds(List.of(serverEmbed.build(), websiteEmbed.build()))
                        .queue(message -> server.setPingUpdateMessage(message.getIdLong()));
            }
            ServerStorageUtility.addServer(server);
        } else {
            channel.retrieveMessageById(server.getPingUpdateMessage())
                    .queue(topicMessage ->
                                    topicMessage.editMessageEmbeds(List.of(serverEmbed.setThumbnail(null).build(), websiteEmbed.build()))
                                            .queue(),
                            new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                channel.sendMessageEmbeds(List.of(serverEmbed.setThumbnail(null).build(), websiteEmbed.build()))
                                        .queue(message
                                        -> server.setPingUpdateMessage(message.getIdLong()));
                                ServerStorageUtility.addServer(server);
                            }));
        }
        return null;
    }

    public static String sendStatusUpdates(Guild guild) {
        Server server = ServerStorageUtility.getServer(guild.getIdLong());
        ArrayList<Topic> topicList = scrapeStatuses("https://www.lordofthecraft.net/forums/");

        String checks = doChecks(server, guild, topicList);

        if(checks != null) {
            return checks;
        }

        TextChannel channel = guild.getTextChannelById(server.getStatusUpdatesChannel());

        assert topicList != null;
        topicList = filterTopics(topicList, TopicType.STATUS_UPDATE);
        ArrayList<MessageEmbed> embedList = filterEmbeds(topicList);

        assert channel != null;
        if(server.getStatusUpdatesMessage() == null) {
            channel.sendMessageEmbeds(embedList).queue(message -> server.setStatusUpdatesMessage(message.getIdLong()));
            ServerStorageUtility.addServer(server);
        } else {
            channel.retrieveMessageById(server.getStatusUpdatesMessage())
                    .queue(topicMessage -> topicMessage.editMessageEmbeds(embedList).queue(),
                            new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                channel.sendMessageEmbeds(embedList).queue(message
                                        -> server.setStatusUpdatesMessage(message.getIdLong()));
                                ServerStorageUtility.addServer(server);
                            })
                    );
        }
        return null;
    }

    public static boolean upCheck(String url) {
        try {
            Jsoup.connect(url).get();
            return true;
        } catch(IOException e) {
            return false;
        }
    }

    public static ArrayList<Topic> scrapeStatuses(String url) {
        ArrayList<Topic> topics = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements listElements = document.getElementsByClass("ipsWidget ipsWidget_vertical cAdvancedStatusUpdatesWidget");

            Element first = listElements.first();
            if(first == null) {
                return null;
            }

            listElements = first.getElementsByClass("ipsDataItem");

            for(Element topicElement : listElements) {

                Element authorElement = topicElement.getElementsByClass("ipsType_medium ipsType_reset").get(0);

                Element authorNameElement = authorElement.getElementsByClass("ipsType_break").get(0);
                String profileName = authorNameElement.text();

                String profileLink = authorNameElement.attr("abs:href");
                Author author = new Author(profileName, null, profileLink);

                Element textElement = topicElement.getElementsByClass("ipsType_richText").get(0);
                String text = textElement.text();

                String replyCount = topicElement.attr("abs:data-commentcount")
                        .replace("https://www.lordofthecraft.net/forums/", "");

                int commentAmount = Integer.parseInt(replyCount);

                Element infoElement = topicElement.getElementsByClass("ipsType_blendLinks ipsPos_middle").get(0);
                Element topicLinkElement = infoElement.getElementsByAttribute("href").get(0);

                String topicLink = topicLinkElement.attr("abs:href");

                Element timeElement = topicElement.getElementsByAttribute("datetime").get(0);

                String postTime = timeElement.attr("title");
                DateTime posTime = DateTimeFormat.forPattern("MM/dd/yy hh:mm  a").withZone(DateTimeZone.UTC).parseDateTime(postTime);

                Topic topic = new Topic(text, topicLink, posTime, author, TopicType.STATUS_UPDATE, commentAmount);

                topic.setEmbed(createStatusEmbed(topic, Color.PINK));

                topics.add(topic);
            }
            return topics;
        } catch (IOException e) {
            System.out.println("Error: Is the website offline?");
        }
        return null;
    }

    public static ArrayList<Topic> scrapeTopics(String url) {
        ArrayList<Topic> topics = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();

            Elements listElements = document.getElementsByClass("ipsDataList ipsDataList_reducedSpacing ipsPad_half");

            int i = 1;
            for(Element listSection : listElements) {

                Elements dataItems = listSection.getElementsByClass("ipsDataItem");

                for(Element topicElement : dataItems) {

                    Element authorElement = topicElement.getElementsByClass("ipsDataItem_icon ipsPos_top").get(0);
                    Element linkElement = authorElement.getElementsByAttribute("href").get(0);
                    Element imageElement = authorElement.getElementsByAttribute("src").get(0);

                    String profileLink = linkElement.attr("abs:href");
                    String imageLink = imageElement.attr("abs:src");
                    String authorName = imageElement.attr("alt");
                    Author author = new Author(authorName, imageLink, profileLink);

                    Element commentElement = topicElement.getElementsByClass("ipsDataItem_main cWidgetComments").get(0);
                    Element topicCommentElement = commentElement.getElementsByAttribute("title").get(0);

                    String commentAmount = topicCommentElement.text();

                    Element infoElement = topicElement.getElementsByClass("ipsType_break ipsContained").get(0);
                    Element topicLinkElement = infoElement.getElementsByAttribute("href").get(0);
                    Element topicNameElement = infoElement.getElementsByClass("ipsDataItem_title").get(0);

                    String topicName = topicNameElement.text();
                    String topicLink = topicLinkElement.attr("abs:href");

                    Element timeElement = topicElement.getElementsByAttribute("datetime").get(0);

                    String postTime = timeElement.attr("title");
                    DateTime posTime = DateTimeFormat.forPattern("MM/dd/yy hh:mm  a").withZone(DateTimeZone.UTC).parseDateTime(postTime);

                    TopicType topicType;
                    if(i == 1) {
                        topicType = TopicType.POPULAR_TOPIC;
                    } else {
                        topicType = TopicType.LATEST_TOPIC;
                    }

                    Topic topic = new Topic(topicName, topicLink, posTime, author, topicType, Integer.parseInt(commentAmount));
                    MessageEmbed embed = createTopicEmbed(topic, Color.PINK);
                    topic.setEmbed(embed);

                    topics.add(topic);
                }
                i++;
            }
            return topics;
        } catch (IOException e) {
            System.out.println("Error: Is the website offline?");
            return null;
        }
    }
}
