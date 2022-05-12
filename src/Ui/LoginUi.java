package Ui;

import Util.Alert;
import domain.User;
import tools.MessageTypeEnum;
import tools.SocketMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class LoginUi extends JFrame {
    private final JTextField usernameInput = new JTextField("", 16);
    private final JTextField passwordInput = new JPasswordField("", 16);
    private final JTextField qqNumInput = new JTextField("", 16);
    private final JPanel qqNumCon = new JPanel();

    private JLabel pageName = new JLabel("<html> <b style='color:blue; font-size: 16px;' >登录界面<b> <html>");
    private final JButton login = new JButton("登录");
    private final JButton register = new JButton("前往注册");
    private final JCheckBox checkbox = new JCheckBox("保存密码");
    boolean isLoginPage = true;
    private User loginUser = null;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private final int port = 5566;
    private final String address = "127.0.0.1";

    public LoginUi(){
        JPanel container = new JPanel(new GridLayout(5, 1));
        JPanel nowPage = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel usernameCon = new JPanel();
        JPanel passwordCon = new JPanel();
        JLabel[] jLabels = {new JLabel("登录账号:   "),new JLabel("登录密码:   "),new JLabel("qq号码:    ")};

        nowPage.add(pageName);

        login.setBackground(new Color(80, 159, 248));
        register.setBackground(new Color(220, 221, 224));

        usernameInput.setPreferredSize(new Dimension(20,25));
        passwordInput.setPreferredSize(new Dimension(20,25));
        qqNumInput.setPreferredSize(new Dimension(20,25));
        usernameCon.setPreferredSize(new Dimension(400,40));

        JPanel footer = new JPanel(new FlowLayout());
        JPanel footLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel footRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        footLeft.add(checkbox);

        footRight.add(login);
        footRight.add(register);
        footer.add(footLeft);
        footer.add(footRight);

        usernameCon.add(jLabels[0]);
        usernameCon.add(usernameInput);
        passwordCon.add(jLabels[1]);
        passwordCon.add(passwordInput);
        qqNumCon.add(jLabels[2]);
        qqNumCon.add(qqNumInput);

        container.add(nowPage);
        container.add(usernameCon);
        container.add(passwordCon);
        container.add(qqNumCon);
        container.add(footer);
        setContentPane( container );
        setSize(300,300);
        centerWindow();
        readPassword();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void open(){ //显示登录界面
        showLoginPage();
        setVisible(true);
        try { //与服务器建立连接
            socket = new Socket(address,port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
           Alert.error("服务器异常！");
           System.exit(0);
        }

        // ---------添加事件
        addWindowListener(new WindowAdapter() { //窗口关闭事件
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("聊天窗口关闭！");
                try {
                    oos.writeObject(new SocketMessage()); //提示服务端我要退出,socket可以关了
                    oos.close();
                    ois.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
        login.addActionListener(e->{ //登录按钮点击
            if (isLoginPage){
               login();
            } else {
                showLoginPage();
            }
        });
        register.addActionListener(e -> { //注册按钮点击
            if (!isLoginPage){
                register();
            } else {
                showRegisterPage();
            }
        });
        qqNumInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {   //qq号只能输入数字
                char c = e.getKeyChar();
                if ((int)c==10 || (int)c==8){ //输入回车或删除 不处理
                    return;
                }
                else if (c<'0'||c>'9'){
                    Alert.error("qq号只能输入数字！");
                    e.consume();
                }
            }
        });
    }

    private void showRegisterPage() {
        setTitle("注册界面");
        pageName.setText("<html> <b style='color:red; font-size: 16px;' >注册界面<b> <html>");
        login.setText("前往登录");
        login.setBackground(new Color(220, 221, 224));
        register.setText("点击注册");
        register.setBackground(new Color(80, 159, 248));
        checkbox.setVisible(false);
        qqNumCon.setVisible(true);
        isLoginPage = false;
    }

    private void showLoginPage() {
        setTitle("登录界面");
        pageName.setText("<html> <b style='color:blue; font-size: 16px;' >登录界面<b> <html>");
        login.setText("登录");
        login.setBackground(new Color(80, 159, 248));
        register.setText("前往注册");
        register.setBackground(new Color(220, 221, 224));
        checkbox.setVisible(true);
        qqNumCon.setVisible(false);
        isLoginPage = true;
    }

    private void login(){
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        if ( CheckUsername(username) && CheckPassword(password) ){
            User user = new User(username,password,null);
            try {
                oos.writeObject(user);
                SocketMessage sm = (SocketMessage)ois.readObject();
                if ( sm.getMessageTypeEnum()== MessageTypeEnum.Login_Success) { //登录成功
                    this.loginUser = (User) sm.getObject();
                    loginSuccess();
                } else { //登录失败
                    Alert.error("用户名或密码错误！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (checkbox.isSelected()){
            savePassword();
        } else {
            clearPassword();
        }
    }
    private void register(){
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        String qqNum = qqNumInput.getText();
        if ( CheckUsername(username) && CheckPassword(password) && CheckQqNum(qqNum) ){
            User user = new User(username,password,qqNum);
            try {
                oos.writeObject(user);
                SocketMessage sm = (SocketMessage)ois.readObject();
                if ( sm.getMessageTypeEnum()== MessageTypeEnum.Register_Success) { //注册成功
                    Alert.info("注册成功，快去登录吧");
                    showLoginPage();
                } else { //注册失败
                    Alert.error("用户名已存在！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loginSuccess(){
        setVisible(false);
        ChatRoomUi chatRoomUi = new ChatRoomUi(this.loginUser,socket,oos,ois);
        chatRoomUi.setVisible(true);
    }

    private boolean CheckQqNum(String qqNum) {
        int len = qqNum.length();
        if ( len<5 || len>10 ){
            Alert.error("qq号长度有误！应为5-10位数字.");
            return false;
        }
        return true;
    }

    private boolean CheckPassword(String password) {
        int len = password.length();
        if ( len<6 || len>15 ){
            Alert.error("密码长度有误！应为6-15位数字.");
            return false;
        }
        return true;
    }

    private boolean CheckUsername(String username) {
        int len = username.length();
        if ( len<2 || len>15 ){
            Alert.error("登录账号长度有误！应为2-12位符号.");
            return false;
        }
        return true;
    }

    private void readPassword(){
        File file = new File("savePassword.txt");
        if ( !file.exists() ) return;
        FileReader fileReader = null;
        BufferedReader bufferedReader  = null;
        try {
            fileReader = new FileReader( file );
            bufferedReader = new BufferedReader(fileReader);
            String s = bufferedReader.readLine();
            String[] split = s.split(",");
            usernameInput.setText(split[0]);
            passwordInput.setText(split[1]);
            checkbox.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(null,bufferedReader);
            closeFile(null,fileReader);
        }
    }

    private void clearPassword() {
        File file = new File("savePassword.txt");
        file.delete();
    }

    private void savePassword() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("savePassword.txt");
            fileWriter.write(username+","+password);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(fileWriter,null);
        }
    }

    private void centerWindow() {
        Toolkit toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation ((int) (screenSize.getWidth()-getWidth())/2,(int) (screenSize.getHeight() - getHeight())/2);
    }

    private void closeFile(Writer fs,Reader fr){
        if ( fs!=null ) {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ( fr!=null ) {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
