package tech.adelemphii.forumscraper.objects;

import org.jetbrains.annotations.Nullable;

public class Author {

    private String name;
    private String image;
    private String url;

    public Author(String name, @Nullable String image, String url) {
        this.name = name;
        this.image = image;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
