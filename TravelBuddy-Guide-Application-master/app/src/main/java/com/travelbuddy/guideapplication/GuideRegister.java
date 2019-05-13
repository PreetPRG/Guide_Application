package com.travelbuddy.guideapplication;

public class GuideRegister {

    private String guide_name;
    private String current_city;
    private Long gender;
    private String language;
    private Long ratings;
    private boolean available;
    private String experience;
    private String description;

    public GuideRegister(){

    }

    public GuideRegister(String guide_name, String current_city, Long gender, String language, Long ratings, boolean available, String experience, String description) {
        this.guide_name = guide_name;
        this.current_city = current_city;
        this.gender = gender;
        this.language = language;
        this.ratings = ratings;
        this.available = available;
        this.experience = experience;
        this.description = description;
    }

    public String getGuide_name() {
        return guide_name;
    }

    public void setGuide_name(String guide_name) {
        this.guide_name = guide_name;
    }

    public String getCurrent_city() {
        return current_city;
    }

    public void setCurrent_city(String current_city) {
        this.current_city = current_city;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getRatings() {
        return ratings;
    }

    public void setRatings(Long ratings) {
        this.ratings = ratings;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
