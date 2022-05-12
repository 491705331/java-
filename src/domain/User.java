package domain;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String qqNum;
    private boolean isonline;

    public User(String username, String password, String qqNum) {
        this.username = username;
        this.password = password;
        this.qqNum = qqNum;
        this.isonline = false;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", qqNum='" + qqNum + '\'' +
                ", isonline=" + isonline +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQqNum() {
        return qqNum;
    }

    public void setQqNum(String qqNum) {
        this.qqNum = qqNum;
    }

    public boolean isIsonline() {
        return isonline;
    }

    public void setIsonline(boolean isonline) {
        this.isonline = isonline;
    }
}
