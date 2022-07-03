package tech.adelemphii.forumscraper.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.adelemphii.forumscraper.objects.Author;
import tech.adelemphii.forumscraper.objects.Topic;
import tech.adelemphii.forumscraper.objects.TopicType;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScrapeUtility {

    public static EmbedBuilder createTopicEmbed(Topic topic, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(topic.getTitle(), topic.getUrl());
        builder.setColor(color);
        builder.setAuthor(topic.getAuthor().getName(), topic.getAuthor().getUrl(), topic.getAuthor().getImage());
        TemporalAccessor accessor = Instant.ofEpochMilli(topic.getPostDate().getMillis());
        builder.setTimestamp(accessor);

        return builder;
    }

    public static Map<Topic, MessageEmbed> scrapeTopics(String url) {
        Map<Topic, MessageEmbed> topics = new HashMap<>();

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

                    Element infoElement = topicElement.getElementsByClass("ipsType_break ipsContained").get(0);
                    Element topicLinkElement = infoElement.getElementsByAttribute("href").get(0);
                    Element topicNameElement = infoElement.getElementsByClass("ipsDataItem_title").get(0);

                    String topicName = topicNameElement.text();
                    String topicLink = topicLinkElement.attr("abs:href");

                    Element timeElement = topicElement.getElementsByAttribute("datetime").get(0);

                    String postTime = timeElement.attr("title");
                    DateTime posTime = DateTimeFormat.forPattern("MM/dd/yy hh:mm  a").withZone(DateTimeZone.getDefault()).parseDateTime(postTime);

                    TopicType topicType;
                    if(i == 1) {
                        topicType = TopicType.POPULAR_TOPIC;
                    } else {
                        topicType = TopicType.LATEST_TOPIC;
                    }

                    Topic topic = new Topic(topicName, topicLink, posTime, author, topicType);
                    MessageEmbed embed = createTopicEmbed(topic, Color.PINK).build();

                    topics.put(topic, embed);
                }
                i++;
            }


            return topics;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
