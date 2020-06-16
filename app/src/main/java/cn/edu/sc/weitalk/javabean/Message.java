package cn.edu.sc.weitalk.javabean;

public class Message {
    public String msgText;
    public boolean msgType;
    public String date;
    public String name;
    public String header_img;

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

    public void setName(String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public void setHeader_img(String header_img){
        this.header_img=header_img;
    }

    public String getHeader_img(){
        return header_img;
    }
}
