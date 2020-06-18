package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class FriendReqRes extends DataSupport {
    public static final int NEW_FRIEND_REQUEST = 0;
    public static final int NEW_FRIEND_RESPONSE = 1;
    public static final int DELETE_BY_FRIEND = 2;

    private int type;
    private String MyID;
    private String userId;
    private String username;
    private String headUrl;
    private boolean isAgreed;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isAgreed() {
        return isAgreed;
    }

    public void setAgreed(boolean agreed) {
        isAgreed = agreed;
    }

    public String getMyID() {
        return MyID;
    }

    public void setMyID(String myID) {
        MyID = myID;
    }
}
