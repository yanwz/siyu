package org.example;

public class UserMessage {

    private String id;

    private String userId;

    private String content;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return '{' +
                "\"id\":\"" + id + '\"' +
                ", \"userId\":\"" + userId + '\"' +
                ", \"content\":\"" + content + '\"' +
                '}';
    }
}
