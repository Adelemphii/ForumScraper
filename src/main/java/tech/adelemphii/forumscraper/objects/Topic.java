package tech.adelemphii.forumscraper.objects;

import org.joda.time.DateTime;

public class Topic {

    private final String title;
    private final String url;
    private final DateTime postDate;

    private final Author author;
    private final TopicType topicType;

    public Topic(String title, String url, DateTime postDate, Author author, TopicType topicType) {
        this.title = title;
        this.url = url;
        this.postDate = postDate;
        this.author = author;
        this.topicType = topicType;
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

    @Override
    public String toString() {
        return "Topic{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", postDate=" + postDate +
                ", author=" + author +
                ", topicType=" + topicType +
                '}';
    }
}
