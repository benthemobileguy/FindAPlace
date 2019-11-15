package com.fap.bnotion.findaplace;


public class News {
    private String image, title, subtitle, message, webLink;
    public News(){

    }
    public News(String image, String webLink, String message, String title, String subtitle, Long timestamp) {
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.webLink= webLink;
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
