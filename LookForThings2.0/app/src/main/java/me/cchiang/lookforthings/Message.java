package me.cchiang.lookforthings;

/**
 * Created by zschreck on 2/28/17.
 */

public class Message {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String time;
    private boolean fromMe;
    private String sentFrom;

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String time) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.time = time;
        this.fromMe  = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String set_time) {
        this.time = set_time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }
}

