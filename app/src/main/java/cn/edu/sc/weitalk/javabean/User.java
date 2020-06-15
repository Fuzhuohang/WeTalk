package cn.edu.sc.weitalk.javabean;

public class User {
    /**
     * Created by wangsong on 2016/4/24.
     */
    private int img;
    private String username;
    private String pinyin;
    private String firstLetter;

    public User() {
    }

    public User(String firstLetter, int img, String pinyin, String username) {
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
}
