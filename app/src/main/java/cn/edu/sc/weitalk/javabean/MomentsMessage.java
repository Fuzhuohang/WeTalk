package cn.edu.sc.weitalk.javabean;

import android.graphics.Bitmap;
import android.net.Uri;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;

public class MomentsMessage extends DataSupport {
    private String momentID;

    public String getMomentID() {
        return momentID;
    }

    public void setMomentID(String momentID) {
        this.momentID = momentID;
    }

    private  String publisherID;
    private String publisherName;
    private  String Date;
    private  String content;
    private String headshot;//头像
    private String momentImage;//朋友圈消息包含的的图片
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

    public String getHeadshot() {
        return headshot;
    }

    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }

    public String getMomentImage() {
        return momentImage;
    }

    public void setMomentImage(String momentImage) {
        this.momentImage = momentImage;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public static byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
