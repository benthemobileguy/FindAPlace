package com.fap.bnotion.findaplace;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgentPost extends BlogPostId implements Serializable{
    @Exclude private String id;
    public String image_url,  profile_url, title, category, location, price, number_of_toilets, number_of_baths, state, user_id, username, mobile_number, status, price_range, email, area, desc;
    public Date timestamp;
    public ArrayList<String> image_urls;
    public AgentPost(){

}

    public AgentPost(String image_url, String title, String area, String category, String location, String price, String number_of_toilets, String number_of_baths, String state, String user_id, String username, String mobile_number, String status, String email, String desc, String profile_url, String price_range, Date timestamp, ArrayList<String> image_urls) {
        this.image_url = image_url;
        this.title = title;
        this.category = category;
        this.location = location;
        this.price = price;
        this.number_of_toilets = number_of_toilets;
        this.number_of_baths = number_of_baths;
        this.state = state;
        this.user_id = user_id;
        this.username = username;
        this.profile_url = profile_url;
        this.mobile_number = mobile_number;
        this.price_range = price_range;
        this.status = status;
        this.email = email;
        this.desc = desc;
        this.timestamp = timestamp;
        this.image_urls = image_urls;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber_of_toilets() {
        return number_of_toilets;
    }

    public void setNumber_of_toilets(String number_of_toilets) {
        this.number_of_toilets = number_of_toilets;
    }

    public String getNumber_of_baths() {
        return number_of_baths;
    }

    public void setNumber_of_baths(String number_of_baths) {
        this.number_of_baths = number_of_baths;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
