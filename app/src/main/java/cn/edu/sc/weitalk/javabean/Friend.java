package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class Friend extends DataSupport {

    private String img;
    private String username;
    private String userID;
    private String note;        //没有给好友添加备注时，note = ""; 空串
    private String location;
    private String birthday;
    private String phoneNum;
    private String email;
    private boolean status;
    private String MyID;
    //以下字段非映射字段
    public String pinyin;
    public String firstLetter;


    public Friend() {
    }

    public Friend(String firstLetter, String img, String pinyin, String username) {
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getMyID(){
        return MyID;
    }

    public void setMyID(String myID){
        this.MyID = myID;
    }
}
