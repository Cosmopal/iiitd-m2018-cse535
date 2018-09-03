package com.mc.hw2;

import java.util.Objects;

public class MusicItem {

    //filename = name in case of storage music.
    public String name;
    public String type;
    public int resID;
    public String path;
    public static final String TYPE_RES = "res";
    public static final String TYPE_STORE = "storage";
    public static final String TYPE_ONLINE = "online";

    public MusicItem(String name, String type, int resID) {
        this.name = name;
        this.type = type;
        this.resID = resID;
    }

    public MusicItem(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public MusicItem(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return "MusicItem{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", resID=" + resID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicItem item = (MusicItem) o;
        return resID == item.resID &&
                Objects.equals(name, item.name) &&
                Objects.equals(type, item.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, type, resID);
    }
}
