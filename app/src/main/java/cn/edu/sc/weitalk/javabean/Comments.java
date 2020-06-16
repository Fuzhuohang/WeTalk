package cn.edu.sc.weitalk.javabean;

import org.litepal.crud.DataSupport;

public class Comments extends DataSupport {
    private String momentID;
    private String commentPerID;
    private String content;
    private String commentPerName;

    public String getMomentID() {
        return momentID;
    }

    public void setMomentID(String momentID) {
        this.momentID = momentID;
    }

    public String getCommentPerID() {
        return commentPerID;
    }

    public void setCommentPerID(String commentPerID) {
        this.commentPerID = commentPerID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentPerName() {
        return commentPerName;
    }

    public void setCommentPerName(String commentPerName) {
        this.commentPerName = commentPerName;
    }
}
