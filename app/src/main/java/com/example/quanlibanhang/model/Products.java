package com.example.quanlibanhang.model;

import android.text.TextUtils;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties//vừa dùng database trả về và up lên dtbase
public class Products {
    private String id, name, price, mota, avatar;
    public Products() {
    }

    public Products(String name, String price, String mota, String avatar) {
        this.name = name;
        this.price = price;
        this.mota = mota;
        this.avatar = avatar;
    }

    public Products(String id, String name, String price, String mota, String avatar) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.mota = mota;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

//    public boolean isEmpty(){
//        return  !TextUtils.isEmpty(name) && !TextUtils.isEmpty(price)
//                && !TextUtils.isEmpty(mota) && !TextUtils.isEmpty(avatar);
//    }
}
