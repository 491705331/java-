package dao;

import Util.JDBCutil;
import domain.Group;
import domain.Message;
import domain.User;

import java.util.List;

/**
 * 聊天室的DAO操作
 */
public class DaoUtil {
    public static boolean login(User user) {
        User u = (User) JDBCutil.queryForObject(User.class, "select username,password from user where username=?", user.getUsername());
        return u!=null;
    }

    public static boolean register(User user) {
        return JDBCutil.update("insert into user(username,password,qqNum) values(?,?,?)", user.getUsername(), user.getPassword(), user.getQqNum());
    }

    public static List<Message> gitHistory(int gid) {
        return JDBCutil.queryForList(Message.class, "select mid,username,message,gid from message where gid=?",gid);
    }

    public static String getQqNumByUsername(String username) {
        User user = (User) JDBCutil.queryForObject(User.class, "select qqNum from user where username=?", username);
        return user.getQqNum();
    }
    public static User getUserByUsername(String username) {
        return (User) JDBCutil.queryForObject(User.class, "select username,password,qqNum from user where username=?", username);
    }

    public static void addMessage(String username, String message,int gid) {
        JDBCutil.update("insert into message(username,message,gid) values(?,?,?)", username,message,gid);
    }

    public static List<Group> getGroups() {
        return JDBCutil.queryForList(Group.class,"select gid,gname from `group`");
    }
}
