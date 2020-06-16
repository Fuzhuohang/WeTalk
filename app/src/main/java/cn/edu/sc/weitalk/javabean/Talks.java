package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class Talks extends DataSupport {


    public String TalksName;
    public String FriendHeaderURL;
    public String LastMessage;

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
}
