package tech.adelemphii.forumscraper.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.joda.time.DateTime;

public class Topic {

    private final String title;
    private final String url;
    private final DateTime postDate;
    private final int comments;

    private final Author author;
    private final TopicType topicType;

    private MessageEmbed embed;

    public Topic(String title, String url, DateTime postDate, Author author, TopicType topicType, int comments) {
        this.title = title;
        this.url = url;
        this.postDate = postDate;
        this.author = author;
        this.topicType = topicType;
        this.comments = comments;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public DateTime getPostDate() {
        return postDate;
    }

    public Author getAuthor() {
        return author;
    }

    public TopicType getTopicType() {
        return topicType;
    }

    public int getCommentCount() {
        return comments;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", postDate=" + postDate +
                ", comments=" + comments +
                ", author=" + author +
                ", topicType=" + topicType +
                ", embed=" + embed +
                '}';
    }
}
