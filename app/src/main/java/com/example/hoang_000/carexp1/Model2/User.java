package com.example.hoang_000.carexp1.Model2;

/**
 * Created by hoang_000 on 20/03/2018.
 */

public class User {

    private String email,password,name,phone,avatarUrl;
    public User() {
    }

    /**
     *  Ham khoi tao thong tin ve user su dung dien thoai
     * @param email     Dia chi email. Vi du hoang@gmail.com
     * @param password
     * @param name
     * @param phone
     * @param avatarUrl
     */
    public User(String email, String password, String name, String phone,String avatarUrl,String rates) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.avatarUrl=avatarUrl;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }



}

