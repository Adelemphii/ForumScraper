package tech.adelemphii.forumscraper.objects;

public enum TopicType {

    POPULAR_TOPIC("Popular Topic"),
    LATEST_TOPIC("Latest Topic"),
    STATUS_UPDATE("Status Update");

    private String name;

    TopicType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TopicType{" + name + "}";
    }
}
