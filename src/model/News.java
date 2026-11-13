package model;

/**
 * A single news article shown in the news feed.
 */
public class News {

    private int id;
    private String title;
    private String source;
    private String category;
    private String date;
    private String url;
    private String summary;

    public News(int id, String title, String source, String category,
                String date, String url, String summary) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.category = category;
        this.date = date;
        this.url = url;
        this.summary = summary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return title;
    }
}
