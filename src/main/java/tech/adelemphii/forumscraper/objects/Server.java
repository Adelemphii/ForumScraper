package tech.adelemphii.forumscraper.objects;

import org.jetbrains.annotations.Nullable;

public class Server {

    private final long serverID;
    private Long popularTopicsChannel;
    private Long latestTopicsChannel;
    private Long statusUpdatesChannel;

    private Long popularTopicMessage;
    private Long latestTopicsMessage;
    private Long statusUpdatesMessage;

    public Server(long serverID, @Nullable Long popularTopicsChannel, @Nullable Long latestTopicsChannel, @Nullable Long statusUpdatesChannel,
                  @Nullable Long popularTopicMessage, @Nullable Long latestTopicsMessage, @Nullable Long statusUpdatesMessage) {
        this.serverID = serverID;
        this.popularTopicsChannel = popularTopicsChannel;
        this.latestTopicsChannel = latestTopicsChannel;
        this.statusUpdatesChannel = statusUpdatesChannel;
        this.popularTopicMessage = popularTopicMessage;
        this.latestTopicsMessage = latestTopicsMessage;
        this.statusUpdatesMessage = statusUpdatesMessage;
    }

    public long getServerID() {
        return serverID;
    }

    public Long getPopularTopicsChannel() {
        return popularTopicsChannel;
    }

    public void setPopularTopicsChannel(long popularTopicsChannel) {
        this.popularTopicsChannel = popularTopicsChannel;
    }

    public Long getLatestTopicsChannel() {
        return latestTopicsChannel;
    }

    public void setLatestTopicsChannel(long latestTopicsChannel) {
        this.latestTopicsChannel = latestTopicsChannel;
    }

    public Long getStatusUpdatesChannel() {
        return statusUpdatesChannel;
    }

    public void setStatusUpdatesChannel(long statusUpdatesChannel) {
        this.statusUpdatesChannel = statusUpdatesChannel;
    }

    public Long getPopularTopicMessage() {
        return popularTopicMessage;
    }

    public void setPopularTopicMessage(long popularTopicMessage) {
        this.popularTopicMessage = popularTopicMessage;
    }

    public Long getLatestTopicsMessage() {
        return latestTopicsMessage;
    }

    public void setLatestTopicsMessage(long latestTopicsMessage) {
        this.latestTopicsMessage = latestTopicsMessage;
    }

    public Long getStatusUpdatesMessage() {
        return statusUpdatesMessage;
    }

    public void setStatusUpdatesMessage(long statusUpdatesMessage) {
        this.statusUpdatesMessage = statusUpdatesMessage;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverID=" + serverID +
                ", popularTopicsChannel=" + popularTopicsChannel +
                ", latestTopicsChannel=" + latestTopicsChannel +
                ", statusUpdatesChannel=" + statusUpdatesChannel +
                ", popularTopicMessage=" + popularTopicMessage +
                ", latestTopicsMessage=" + latestTopicsMessage +
                ", statusUpdatesMessage=" + statusUpdatesMessage +
                '}';
    }
}
