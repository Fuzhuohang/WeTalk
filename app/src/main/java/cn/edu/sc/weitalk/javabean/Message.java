package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class Message extends DataSupport {
    public String msgText;
    public boolean msgType;
    public long date;
    public String sendID;
    public String receiveID;
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

    public void setDate(long date){
        this.date=date;
    }

    public long getDate(){
        return date;
    }

    public void setSendName(String sendID){
        this.sendID=sendID;
    }

    public String getSendName(){
        return sendID;
    }

    public void setReceiveName(String receiveID){
        this.receiveID=receiveID;
    }

    public String getReceiveName(){
        return receiveID;
    }

    public void setRead(boolean isRead){
        this.isRead=isRead;
    }

    public boolean getRead(){
        return isRead;
    }
}
