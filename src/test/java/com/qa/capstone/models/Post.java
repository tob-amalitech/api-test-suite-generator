package com.qa.capstone.models;

/**
 * POJO class representing a Post from JSONPlaceholder API.
 * Fields: userId, id, title, body
 */
public class Post {

    private int userId;
    private int id;
    private String title;
    private String body;

    /**
     * Default constructor.
     */
    public Post() {
    }

    /**
     * Constructor with all fields.
     *
     * @param userId the user ID
     * @param id     the post ID
     * @param title  the post title
     * @param body   the post body
     */
    public Post(int userId, int id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    /**
     * Constructor with userId, title, and body (for POST/PATCH requests).
     *
     * @param userId the user ID
     * @param title  the post title
     * @param body   the post body
     */
    public Post(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
