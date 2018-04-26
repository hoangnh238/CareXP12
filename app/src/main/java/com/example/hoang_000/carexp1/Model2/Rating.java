package com.example.hoang_000.carexp1.Model2;

/**
 * Created by hoang_000 on 04/04/2018.
 */

public class Rating {

    private String emailUser;
    private String placenameRate;
    private String userID;
    private String rateValue;
    private String comments;
    private String placeRatingID;
    public Rating() {
    }

    public Rating(String emailUser, String placenameRate, String userID, String rateValue, String comments, String placeRatingID) {
        this.emailUser = emailUser;
        this.placenameRate = placenameRate;
        this.userID = userID;
        this.rateValue = rateValue;
        this.comments = comments;
        this.placeRatingID = placeRatingID;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPlacenameRate() {
        return placenameRate;
    }

    public void setPlacenameRate(String placenameRate) {
        this.placenameRate = placenameRate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPlaceRatingID() {
        return placeRatingID;
    }

    public void setPlaceRatingID(String placeRatingID) {
        this.placeRatingID = placeRatingID;
    }
}
