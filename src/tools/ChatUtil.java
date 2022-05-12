package tools;

import dao.DaoUtil;
import domain.Message;
import domain.User;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 这是一个中间件，用于服务器和客户端的交互
 * 存储了所有用户的socket对象
 */
public class ChatUtil {
    private static JTextField onlineText; //当前类管理的online文本框
    private static int maxConnection; //最大连接数
    private static HashMap<User, Socket> userMap = new HashMap<>();
    private static HashMap<User, ObjectOutputStream> oosMap = new HashMap<>();
    private static HashMap<User, ObjectInputStream> oisMap = new HashMap<>();

    /**
     * 当一个用户发送了消息时，将消息内容通过已存储的socket对象，通知所有用户
     * @param user 发送的用户
     * @param message 发送的内容
     */
    public static void sendMessage(User user,Message message) {  //用户发送消息
        synchronized (userMap) {
            DaoUtil.addMessage(user.getUsername(),message.getMessage(),message.getGid());
            SocketMessage sm = new SocketMessage();
            sm.setMessageTypeEnum(MessageTypeEnum.Return_Message);
            Map<String,Object> sendMap = new HashMap<>();
            sendMap.put("sender",user);
            sendMap.put("message",message);
            sm.setObject(sendMap);
            try {
                for (User u:userMap.keySet()) { //将数据通知给其他客户端
                    if (Objects.equals(u.getUsername(), user.getUsername()))
                        continue;
                    oosMap.get(u).writeObject(sm);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void returnOnlines() { //更新所有客户端---在线用户列表
        synchronized (userMap) {
            SocketMessage sm = new SocketMessage();
            List<User> onlines = new ArrayList<>(userMap.keySet());
            sm.setMessageTypeEnum(MessageTypeEnum.Return_Onlines);
            sm.setObject(onlines);
            for (User u : oosMap.keySet()) {
                try {
                    oosMap.get(u).writeObject(sm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回群的历史聊天记录
     * @param gid 群id
     */
    public static List<Message> gitHistory(int gid) {
        return DaoUtil.gitHistory(gid);
    }


    public static void addClient(User user, Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        synchronized (userMap) {
            userMap.put(user, socket);
            oosMap.put(user, oos);
            oisMap.put(user, ois);
            if (userMap.size() > maxConnection) {
                try {
                    oos.writeObject(new SocketMessage(MessageTypeEnum.Refuse_Connect, "服务器已到达最大连接数！"));
                    delClient(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            returnOnlines(); //更新在线列表
            updateOnlineText();  //更新服务端在线数量
        }
    }

    public static void delClient(User user) {
        synchronized (userMap) {
            try {
                oosMap.get(user).close();
                oisMap.get(user).close();
                userMap.get(user).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            oosMap.remove(user);
            oisMap.remove(user);
            userMap.remove(user);
            returnOnlines(); //更新在线列表
            updateOnlineText(); //更新服务端在线数量
        }
    }

    public static void closeAll() {
        synchronized (userMap) {
            Set<User> usersSet = userMap.keySet();
            ArrayList<User> users = new ArrayList<>(usersSet);
            for (User user : users) { //不使用set循环是为了防止并行修改
                delClient(user);
            }
        }
    }

    private static void updateOnlineText() {
        if (onlineText!=null)
            onlineText.setText("当前在线人数 "+userMap.size() + "人");
    }
    public static void setOnlineText(JTextField text) {
        onlineText = text;
    }
    public static int getSize() {
        return userMap.size();
    }
    public static String getNowDate() {
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        return ft.format(dNow);
    }

    /**
     * 设置允许多少个用户同时在线
     * @param max
     */
    public static void setMaxConnection(int max) {
        ChatUtil.maxConnection = max;
    }
}
