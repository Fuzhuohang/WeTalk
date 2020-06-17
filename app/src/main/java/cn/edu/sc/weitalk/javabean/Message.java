package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class Message extends DataSupport {
    public String msgText;
    public boolean msgType;
    public String date;
    public String sendName;
    public String receiveName;
    public boolean isRead;

    public void setMsgText(String msgText){
        this.msgText=msgText;
    }

    public String getMsgText(){
        return msgText;
    }

    public void setMsgType(boolean msgType){
        this.msgType=msgType;
    }

    public boolean getMsgType(){
        return msgType;
    }

    public void setDate(String date){
        this.date=date;
    }

    public String getDate(){
        return date;
    }

    public void setSendName(String sendName){
        this.sendName=sendName;
    }

    public String getSendName(){
        return sendName;
    }

    public void setReceiveName(String receiveName){
        this.receiveName=receiveName;
    }

    public String getReceiveName(){
        return receiveName;
    }

    public void setRead(boolean isRead){
        this.isRead=isRead;
    }

    public boolean getRead(){
        return isRead;
    }
}