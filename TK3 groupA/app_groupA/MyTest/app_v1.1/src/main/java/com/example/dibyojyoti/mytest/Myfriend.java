package com.example.dibyojyoti.mytest;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * This class maintains the details of Friends.
 * Created by Dell on 20.06.2016.
 */
public class Myfriend {
    private String MobNumber;
    private String Name;
    private com.google.android.gms.maps.model.LatLng LatLng;
    boolean isOnline;
    private ArrayList<Myfriend> friendFriends = new ArrayList<Myfriend>();

    Myfriend(String MobNumber) {
        this.MobNumber = MobNumber;
    }

    public com.google.android.gms.maps.model.LatLng getLatLng() {
        return this.LatLng;
    }

    public String getMobNumber() {
        return MobNumber;
    }

    public String getName() {
        return Name;
    }

    public void setMobNumber(String mobNumber) {
        this.MobNumber = mobNumber;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLatLng(double lat, double lng) {
        LatLng = new LatLng(lat, lng);
    }

    public ArrayList<Myfriend> getFriendFriends() {
        return friendFriends;
    }

    public void setFriendFriends(ArrayList<Myfriend> friendFriends) {
        this.friendFriends = friendFriends;
    }
}

