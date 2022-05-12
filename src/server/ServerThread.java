package server;

import dao.DaoUtil;
import domain.Group;
import domain.Message;
import domain.User;
import tools.ChatUtil;
import tools.MessageTypeEnum;
import tools.SocketMessage;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * 这个类负责与一个单一用户进行交换数据
 */
public class ServerThread extends Thread{
    private JTextArea logArea;
    private Socket socket; //用户的socket对象
    private User user; //当前线程管理的用户
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ServerThread(Socket socket, JTextArea logArea){
        this.socket = socket;
        this.logArea = logArea;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.user==null) { //等待用户登录成功
            waitLogin();
        }
        while (!socket.isClosed()) { //不断响应客户端请求
            try {
                SocketMessage sm = (SocketMessage) ois.readObject();
                if (sm.getMessageTypeEnum()==MessageTypeEnum.Send_Message) { //用户发送消息
                    Message message = (Message) sm.getObject();
                    logArea.append("["+ChatUtil.getNowDate()+"] "+user.getUsername()+" 发送了一条消息："+message.getMessage()+"\n");
                    ChatUtil.sendMessage(user,message);
                }
                else if (sm.getMessageTypeEnum()==MessageTypeEnum.Get_Onlines) {
                    ChatUtil.returnOnlines();
                }
                else if (sm.getMessageTypeEnum()==MessageTypeEnum.Get_History) { //用户请求历史消息
                    int groupId = (int) sm.getObject();
                    List<Message> messages = ChatUtil.gitHistory(groupId);
                    sm.setMessageTypeEnum(MessageTypeEnum.Return_History);
                    sm.setObject(messages);
                    oos.writeObject(sm);
                } else if (sm.getMessageTypeEnum()==MessageTypeEnum.Exit) { //退出
                    delClient();
                }
                else if (sm.getMessageTypeEnum()==MessageTypeEnum.Get_Groups) { //用户请求群组列表
                    List<Group> groups = DaoUtil.getGroups();
                    sm.setMessageTypeEnum(MessageTypeEnum.Return_Groups);
                    sm.setObject(groups);
                    oos.writeObject(sm);
                }
            } catch (Exception e) {
                break; //线程结束
            }
        }
    }

    private void waitLogin() { //等待登录
        try {
            Object o = ois.readObject(); //读取客户端发送的对象
            SocketMessage sm = new SocketMessage(); //发送给客户端的对象
            if (o instanceof User) {
                if (((User) o).getQqNum() != null){ //为注册操作
                    boolean flag = DaoUtil.register((User) o);
                    if (flag)
                        sm.setMessageTypeEnum(MessageTypeEnum.Register_Success); //注册成功
                    else
                        sm.setMessageTypeEnum(MessageTypeEnum.Register_UsernameExist); //用户名已存在
                    oos.writeObject(sm); //发送消息给客服端
                }
                else { //为登录操作
                    boolean flag = DaoUtil.login((User) o);
                    if (flag) {
                        sm.setMessageTypeEnum(MessageTypeEnum.Login_Success);
                        this.user = DaoUtil.getUserByUsername(((User) o).getUsername()); //查找完整的用户数据并返回
                        sm.setObject(this.user); //为socketMessage设置数据返回给客户端
                        oos.writeObject(sm); //发送消息给客服端
                        addClient(this.user,socket,oos,ois);
                    }
                    else {
                        sm.setMessageTypeEnum(MessageTypeEnum.Login_Error);
                        oos.writeObject(sm); //发送消息给客服端
                    }
                }
            }
            else if (o instanceof SocketMessage) { //用户不进行登录，关闭了客户端
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("waitLogin error");
            this.user = new User();
        }
    }

    private void addClient(User user,Socket socket,ObjectOutputStream oos,ObjectInputStream ois) {
        logArea.append("["+ChatUtil.getNowDate()+"] "+user.getUsername()+" 上线啦"+"\n");
        ChatUtil.addClient(user,socket,oos,ois);
    }
    private void delClient() {
        logArea.append("["+ChatUtil.getNowDate()+"] "+user.getUsername()+" 离开聊天室"+"\n");
        ChatUtil.delClient(user);
    }
}
