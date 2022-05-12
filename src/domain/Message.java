package domain;

import java.io.Serializable;

public class Message implements Serializable {
    private int mid;
    private String username;
    private String message;
    private int gid;

    public Message() {
    }

    public Message(String username, String message, int gid) {
        this.username = username;
        this.message = message;
        this.gid = gid;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mid=" + mid +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", gid=" + gid +
                '}';
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
