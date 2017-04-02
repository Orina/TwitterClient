package me.elmira.simpletwitterclient.model;

/**
 * Created by elmira on 3/26/17.
 */

public class EntityMedia {

    private String type;
    private String mediaUrl;

    public EntityMedia(String type, String mediaUrl) {
        this.type = type;
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
