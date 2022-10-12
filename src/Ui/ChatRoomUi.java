package Ui;

import Div.ChatContent;
import Div.GroupDiv;
import Div.TitleBar;
import Div.UserDiv;
import Util.Alert;
import dao.DaoUtil;
import domain.Group;
import domain.Message;
import domain.User;
import tools.MessageTypeEnum;
import tools.SocketMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ChatRoomUi extends JFrame {
    private int win_height = 700;
    private int win_width = 1100;
    private final int left_width = 200;
    private int chatSectionHeight;

    private final JPanel chatSection = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
    private final JScrollPane scrollChat = new JScrollPane(chatSection,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);;
    private final JPanel onlineContainer = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    private final JPanel groupsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    private final JLabel chatTitle = new JLabel();
    private final JPanel chatTitleDiv = new JPanel(new FlowLayout(FlowLayout.CENTER));

    private final JButton sendBtn = new JButton("发送");
    private final JTextField sendInput = new JTextField();
    private User user;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final Socket socket;

    public static int nowGroupId=1; // 第一个群的id

    public ChatRoomUi(User loginUser, Socket socket, ObjectOutputStream oos,ObjectInputStream ois) {
        setTitle("聊天室");
        setResizable(false);
        this.user = loginUser;
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
        initUi();
        addAction();
        try {
            oos.writeObject(new SocketMessage(MessageTypeEnum.Get_Groups,null)); //请求获取群列表
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread clientThread = new Thread(this::refresh);
        clientThread.start();
    }

    private void refresh() { //不断接收服务器的响应
        while (!socket.isClosed()) {
            try {
                SocketMessage sm = (SocketMessage) ois.readObject();
                //服务器返回一条消息
                if (sm.getMessageTypeEnum()== MessageTypeEnum.Return_Message) {
                    Map<String,Object> sendMap = (Map<String, Object>) sm.getObject();
                    Message message = (Message) sendMap.get("message");
                    if (message.getGid()==nowGroupId) {
                        User sender = (User) sendMap.get("sender");
                        ChatContent chatContent = new ChatContent( message.getMessage(),sender.getQqNum(),false,win_width-left_width-20);
                        addMessageInUi(chatContent);
                    }
                }
                //服务器返回历史消息
                else if (sm.getMessageTypeEnum()== MessageTypeEnum.Return_History) {
                    chatSection.removeAll();
                    List<Message> messages = (List<Message>) sm.getObject();
                    chatSectionHeight = 0;
                    chatSection.setPreferredSize(new Dimension(win_width-left_width,chatSectionHeight)); //!!!!!!
                    for (int i=0; i<messages.size(); ++i) {
                        String qqNum = DaoUtil.getQqNumByUsername(messages.get(i).getUsername());
                        ChatContent chatContent = new ChatContent( messages.get(i).getMessage(),qqNum,messages.get(i).getUsername().equals(this.user.getUsername()),win_width-left_width-20);
                        addMessageInUi(chatContent);
                    }
                }
                //服务器返回在线列表
                else if (sm.getMessageTypeEnum()== MessageTypeEnum.Return_Onlines) {
                    List<User> users = (List<User>) sm.getObject();
                    onlineContainer.removeAll();
                    for (int i=0; i<users.size(); ++i) {
                        UserDiv userDiv = new UserDiv(users.get(i).getUsername(), users.get(i).getQqNum(), left_width, 35);
                        onlineContainer.add(userDiv);
                    }
                }
                //服务器返回群组列表
                else if (sm.getMessageTypeEnum()==MessageTypeEnum.Return_Groups) {
                    initGroup(sm);
                    oos.writeObject(new SocketMessage(MessageTypeEnum.Get_History,nowGroupId)); //加载群以后，加载第一个群的历史消息
                }
                //服务器返回拒绝连接
                else if (sm.getMessageTypeEnum()==MessageTypeEnum.Refuse_Connect) {
                    Alert.error((String) sm.getObject());
                }
                setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        Alert.error("服务器已关闭~");
        System.exit(0);
    }

    private void sendMessage(){
        String text = sendInput.getText();
        ChatContent chatContent = new ChatContent(text, this.user.getQqNum(), true, win_width-left_width-20);
        addMessageInUi(chatContent);
        sendInput.setText("");
        Message message = new Message(user.getUsername(), text, nowGroupId);
        try {
            SocketMessage sm = new SocketMessage(MessageTypeEnum.Send_Message, message);
            oos.writeObject(sm);  //-通知服务器我发送了消息
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);
    }

    private void setChatTitle(String title) {
        chatTitle.setText("<html> <t style='font-size:10px;font-weight:bold;'>" + title + "</t> <html>");
    }

    private void addMessageInUi(ChatContent chatContent){
        chatSection.add(chatContent);
        scrollChat.getViewport().setViewPosition(new Point(0, scrollChat.getVerticalScrollBar().getMaximum()));
        chatSectionHeight += 45;
        chatSection.setPreferredSize(new Dimension(chatSection.getWidth(),chatSectionHeight));
    }

    private void addAction(){
        sendBtn.addActionListener( e-> sendMessage() );
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("聊天窗口关闭！");
                exitChatRoom();
                System.exit(0);
            }
        });
        sendInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((int)c==10) {
                    sendMessage();
                }
            }
        });
    }

    private void initGroup(SocketMessage sm) {
        groupsContainer.removeAll();
        List<Group> groups = (List<Group>) sm.getObject();
        nowGroupId = groups.get(0).getGid();
        setChatTitle(groups.get(0).getGname());
        for (int i=0; i<groups.size(); ++i) {
            GroupDiv groupDiv = new GroupDiv(groups.get(i).getGid(), groups.get(i).getGname(), left_width, 50);
            groupsContainer.add( groupDiv );
            int finalI = i;
            int finalI2 = i;
            groupDiv.addActionListener(e -> {
                try {
                    if (groups.get(finalI).getGid()==nowGroupId)
                        return;
                    oos.writeObject(new SocketMessage(MessageTypeEnum.Get_History,groups.get(finalI).getGid()));
                    setChatTitle(groups.get(finalI2).getGname());
                    setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        setVisible(true);
    }

    private void initUi() {
        JPanel container = new JPanel(new BorderLayout(0,0));

        JPanel leftContainer = new JPanel(new FlowLayout());
        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        leftContainer.setPreferredSize( new Dimension(left_width,win_height));
        leftContainer.setBackground(Color.black);
        rightContainer.setPreferredSize( new Dimension(win_width-left_width,win_height));

        container.add(leftContainer,BorderLayout.WEST);
        container.add(rightContainer,BorderLayout.CENTER);
        setContentPane(container);

// left
        TitleBar myInfoTitle = new TitleBar("我的信息", left_width, 30);
        TitleBar groupTitle = new TitleBar("群组信息", left_width, 30);
        TitleBar onLineTitle = new TitleBar("在线列表", left_width, 30);

        JPanel myInfoContainer = getMyInfoContainer();
        groupsContainer.setPreferredSize(new Dimension(left_width,150));
        groupsContainer.setBackground(Color.black);

//        JPanel onlineContainer = getOnlineContainer(onlines);
        onlineContainer.setPreferredSize(new Dimension(left_width,win_height-320));
        onlineContainer.setBackground(Color.black);

        JScrollPane scrollOnline = new JScrollPane(onlineContainer,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollOnline.setPreferredSize(new Dimension(left_width,win_height-300));

        leftContainer.add(myInfoTitle);
        leftContainer.add(myInfoContainer);
        leftContainer.add(groupTitle);
        leftContainer.add( groupsContainer);
        leftContainer.add(onLineTitle);
        leftContainer.add(scrollOnline);


// right
        chatTitleDiv.setPreferredSize(new Dimension(win_width-left_width,(int)(win_height*0.05)));
        chatTitleDiv.add(chatTitle);

        scrollChat.setPreferredSize(new Dimension(win_width-left_width,(int)(win_height*0.84)));
        JPanel sendSection = getSendSection(); //发送区
        rightContainer.add(chatTitleDiv);
        rightContainer.add(scrollChat);
        rightContainer.add(sendSection);
        setSize(win_width,win_height);
        centerWindow();
        setVisible(true);
    }
    private void centerWindow() {
        Toolkit toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation ((int) (screenSize.getWidth()-getWidth())/2,(int) (screenSize.getHeight() - getHeight())/2);
    }
    private JPanel getMyInfoContainer() {
        JPanel myInfoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        myInfoContainer.setPreferredSize(new Dimension(left_width,60));
        myInfoContainer.setBackground(Color.black);
        ImageIcon headerImg = null;
        try {
            headerImg = new ImageIcon(new URL("https://q.qlogo.cn/g?b=qq&nk="+this.user.getQqNum()+"&s=100"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        headerImg.setImage(headerImg.getImage().getScaledInstance(40, 40,4 ));
        JLabel myInfoLabel = new JLabel("<html> <b style='color:white;font-size:12px;'>"+this.user.getUsername()+"<b> </html>" , headerImg,JLabel.LEFT);
        myInfoLabel.setHorizontalTextPosition(JLabel.RIGHT);
        myInfoLabel.setVerticalTextPosition(JLabel.CENTER);
        myInfoContainer.add(myInfoLabel);
        return myInfoContainer;
    }

    private JPanel getSendSection() {
        JPanel sendSection = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        int sendSectionHeight = (int)(win_height*0.1)-20;
        sendSection.setPreferredSize(new Dimension(win_width-left_width-10,sendSectionHeight));
        sendInput.setPreferredSize(new Dimension(win_width-left_width-90,sendSectionHeight-20));
        sendSection.add(sendInput);
        sendSection.add(sendBtn);
//        sendBtn.setPreferredSize(new Dimension(55,sendSectionHeight-10));
        return sendSection;
    }

    private void exitChatRoom() {
        try {
            //给服务器发送退出
            oos.writeObject(new SocketMessage(MessageTypeEnum.Exit,null));
            oos.close();
            ois.close();
            socket.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
