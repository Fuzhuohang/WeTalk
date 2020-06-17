package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class MomentsMessage extends DataSupport {
    private  String publisherID;
    private String publisherName;
    private  String Date;
    private  String content;
    private byte[] headshot;//头像
    private byte[] momentImage;//朋友圈消息包含的的图片
    private int likeCounter;

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getHeadshot() {
        return headshot;
    }

    public void setHeadshot(byte[] headshot) {
        this.headshot = headshot;
    }

    public byte[] getMomentImage() {
        return momentImage;
    }

    public void setMomentImage(byte[] momentImage) {
        this.momentImage = momentImage;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }


}
