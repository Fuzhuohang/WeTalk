package cn.edu.sc.weitalk.javabean;

import java.util.Date;

public class Friend {
    /**
     * Created by wangsong on 2016/4/24.
     */
    private int img;
    private String username;
    private String userId;
    private String note;
    private String location;
    private String birthday;
    private String phoneNum;
    private String email;
    private boolean status;
    //以下字段非映射字段
    public String pinyin;
    public String firstLetter;


    public Friend() {
    }

    public Friend(String firstLetter, int img, String pinyin, String username) {
        this.firstLetter = firstLetter;
        this.img = img;
        this.pinyin = pinyin;
        this.username = username;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
