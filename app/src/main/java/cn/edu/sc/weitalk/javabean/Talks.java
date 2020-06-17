package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

import java.sql.Date;


public class Talks extends DataSupport {


    public String TalksName;
    public String FriendHeaderURL;
    public String LastMessage;
    public Date LastMessageDate;
    public int UnReadNum;

    public String getTalksName(){
        return TalksName;
    }

    public void setTalksName(String TalksName){
        this.TalksName=TalksName;
    }

    public String getFriendHeaderURL(){
        return FriendHeaderURL;
    }

    public void setFriendHeaderURL(String FriendHeaderURL){
        this.FriendHeaderURL = FriendHeaderURL;
    }

    public String getLastMessage(){
        return LastMessage;
    }

    public void setLastMessage(String LastMessage){
        this.LastMessage = LastMessage;
    }

    public Date getLastMessageDate(){
        return LastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate){
        this.LastMessageDate=lastMessageDate;
    }

    public int getUnReadNum(){
        return UnReadNum;
    }

    public void setUnReadNum(int unReadNum){
        this.UnReadNum = unReadNum;
    }
}
