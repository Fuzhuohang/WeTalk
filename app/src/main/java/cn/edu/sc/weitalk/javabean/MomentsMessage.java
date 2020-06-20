package cn.edu.sc.weitalk.javabean;

import android.graphics.Bitmap;
import android.net.Uri;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;

public class MomentsMessage extends DataSupport {
    private String momentID;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private long timeStamp;
    public MomentsMessage(){
        likeCounter=0;
        imageCounter=0;
        momentImage=null;
        momentImage2=null;
        momentImage3=null;
    }
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
    private String momentImage;//朋友圈消息包含的的图片URl
    private int likeCounter;

    public String getMomentImage2() {
        return momentImage2;
    }

    public void setMomentImage2(String momentImage2) {
        this.momentImage2 = momentImage2;
    }

    public String getMomentImage3() {
        return momentImage3;
    }

    public void setMomentImage3(String momentImage3) {
        this.momentImage3 = momentImage3;
    }

    public int getImageCounter() {
        return imageCounter;
    }

    public void setImageCounter(int imageCounter) {
        this.imageCounter = imageCounter;
    }

    private String momentImage2;
    private String momentImage3;
    private int imageCounter;
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
