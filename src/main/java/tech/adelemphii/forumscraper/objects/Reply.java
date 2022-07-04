package tech.adelemphii.forumscraper.objects;

public class Reply {

    private final String lang;
    private final int count;

    public Reply(String lang, int count) {
        this.lang = lang;
        this.count = count;
    }

    public String getLang() {
        return lang;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "lang='" + lang + '\'' +
                ", count=" + count +
                '}';
    }
}
