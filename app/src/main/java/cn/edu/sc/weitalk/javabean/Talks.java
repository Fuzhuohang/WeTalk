package cn.edu.sc.weitalk.javabean;

public class Talks {
    public String TalksName;
    public String MyHeaderURL;
    public String FriendHeaderURL;
    public String Message;
    public String UserName;

    public String getTalksName(){
        return TalksName;
    }

    public void setTalksName(String TalksName){
        this.TalksName=TalksName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName){
        this.UserName = UserName;
    }

    public String getMyHeaderURL(){
        return  MyHeaderURL;
    }

    public void setMyHeaderURL(String MyHeaderURL){
        this.MyHeaderURL = MyHeaderURL;
    }

    public String getFriendHeaderURL(){
        return FriendHeaderURL;
    }

    public void setFriendHeaderURL(String FriendHeaderURL){
        this.FriendHeaderURL = FriendHeaderURL;
    }

    public String getMessage(){
        return Message;
    }

    public void setMessage(String Message){
        this.Message = Message;
    }
}
