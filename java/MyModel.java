package com.example.horapp;
 
public class MyModel {
    String topic = "";
    String description = "";
    int imageResourceId;


    public MyModel(String topic, String description, int imageResourceId) {

        this.topic = topic;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}