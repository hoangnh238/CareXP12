package com.example.hoang_000.carexp1.Model2;

/**
 * Created by hoang_000 on 09/04/2018.
 */

public class Favorites {
    private  String nameofplacefav;
    private String placeIDfav;
    private String addressFav;
    private String userIDFav;

    public Favorites() {
    }

    public Favorites(String nameofplacefav, String placeIDfav, String addressFav, String userIDFav) {
        this.nameofplacefav = nameofplacefav;
        this.placeIDfav = placeIDfav;
        this.addressFav = addressFav;
        this.userIDFav = userIDFav;
    }

    public String getNameofplacefav() {
        return nameofplacefav;
    }

    public void setNameofplacefav(String nameofplacefav) {
        this.nameofplacefav = nameofplacefav;
    }

    public String getPlaceIDfav() {
        return placeIDfav;
    }

    public void setPlaceIDfav(String placeIDfav) {
        this.placeIDfav = placeIDfav;
    }

    public String getAddressFav() {
        return addressFav;
    }

    public void setAddressFav(String addressFav) {
        this.addressFav = addressFav;
    }

    public String getUserIDFav() {
        return userIDFav;
    }

    public void setUserIDFav(String userIDFav) {
        this.userIDFav = userIDFav;
    }
}
