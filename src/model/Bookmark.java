package model;

/**
 * A saved bookmark. A bookmark can be created from a news article
 * or added manually by the user.
 */
public class Bookmark {

    private int id;
    private String title;
    private String url;
    private String category;
    private String dateAdded;
    private String notes;

    public Bookmark(int id, String title, String url, String category,
                    String dateAdded, String notes) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.category = category;
        this.dateAdded = dateAdded;
        this.notes = notes;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return title;
    }
}
