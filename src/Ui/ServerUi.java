package Ui;

import Div.OptionBtn;
import server.ServerThread;
import tools.ChatUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerUi extends JFrame implements ActionListener {
    JPanel left = new JPanel(new GridLayout(25,1));
    JPanel right = new JPanel(new FlowLayout());
    JButton start = new OptionBtn("启动服务");
    JButton stop = new OptionBtn("停止服务");
    JButton sava = new OptionBtn("保存日志");
    JButton exit = new OptionBtn("关闭程序");
    JTextArea logArea = new JTextArea(); //日志文本域
    JTextField onlineText; //在线人数
    JTextField ipText; //ip地址
    JTextField portText; //端口号
    JTextField maxText; //最大连接数
    String logPath = "log.txt"; //保存的日志文件

    ServerSocket serverSocket; //服务器socket对象
    int backlog = 20; //默认最大连接数
    int port = 5566; //默认端口号
    String address = "127.0.0.1"; //默认ip地址

    Thread startThread; //该线程用于启动服务器

    public ServerUi(){ //构造函数初始化界面
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        setContentPane(container);

        initLeftUi();
        initRightUi();

        add(left);
        add(right);
        setSize(550,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void  open() {
        setVisible(true);
    }
    private void startServer() { //点击按钮后用线程启动该方法
        try { //初始化serverSocket
            address = ipText.getText();
            port = Integer.parseInt(portText.getText());
            backlog = Integer.parseInt(maxText.getText());
            serverSocket = new ServerSocket(port,backlog, InetAddress.getByName(address)); //启动
            ChatUtil.setOnlineText(onlineText);
            ChatUtil.setMaxConnection(backlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!serverSocket.isClosed()) { //不断接收客户端连接
            Socket accept;
            try {
                accept= serverSocket.accept();
                ServerThread serverThread = new ServerThread(accept,logArea);
                serverThread.start();
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * 按钮点击事件
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) { //实现ActionListener接口
        if (Objects.equals(e.getActionCommand(), "启动服务")) {
            logArea.append("["+ChatUtil.getNowDate()+"] 服务器已启动："+this.address+":"+this.port+"\n");
            ipText.setEditable(false);
            portText.setEditable(false);
            maxText.setEditable(false);
            start.setEnabled(false);
            stop.setEnabled(true);
            Runnable runnable = this::startServer;
            startThread = new Thread(runnable);
            startThread.start(); //启动服务线程
        } else if (Objects.equals(e.getActionCommand(),"停止服务")) {
            ChatUtil.closeAll();
            logArea.append("["+ChatUtil.getNowDate()+"] 服务器已关闭\n");
            ipText.setEditable(true);
            portText.setEditable(true);
            maxText.setEditable(true);
            start.setEnabled(true);
            stop.setEnabled(false);
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (Objects.equals(e.getActionCommand(),"关闭程序")) {
            ChatUtil.closeAll();
            try {
                if (serverSocket!=null)
                    serverSocket.close();
                saveLog(); //保存日志
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        } else if (Objects.equals(e.getActionCommand(),"保存日志")) {
            saveLog();
        }
    }

    private void saveLog () { //保存日志
        try {
            File file = new File(logPath);
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = null;
            if (file.exists()){
                bufferedReader = new BufferedReader(new FileReader(file));
                while (bufferedReader.ready()) {
                    sb.append( bufferedReader.readLine()+"\n" );
                }
            }
            sb.append( logArea.getText() );
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(sb.toString());

            if (bufferedReader!=null)
                bufferedReader.close();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLeftUi(){
        Font labelFont = new Font("宋体", Font.PLAIN, 12);
        left.setPreferredSize(new Dimension(100,600));
        JLabel usersLabel = new JLabel("当前在线用户：");
        usersLabel.setFont(labelFont);
        JLabel ipLabel = new JLabel("ip地址：");
        ipLabel.setFont(labelFont);
        JLabel portLabel = new JLabel("当前端口：");
        portLabel.setFont(labelFont);
        JLabel maxLabel = new JLabel("最大连接数：");
        maxLabel.setFont(labelFont);

        onlineText = new JTextField(10);
        onlineText.setEditable(false);
        ipText = new JTextField(10);
        ipText.setText(address); //设置为默认地址
        portText = new JTextField(10);
        portText.setText(String.valueOf(port));  //设置为默认端口
        maxText = new JTextField(10);
        maxText.setText(String.valueOf(backlog)); //设置最大连接数

        left.add(usersLabel);
        left.add(onlineText);
        left.add(ipLabel);
        left.add(ipText);
        left.add(portLabel);
        left.add(portText);
        left.add(maxLabel);
        left.add(maxText);
    }

    private void initRightUi() {
        right.setPreferredSize(new Dimension(400,600));
        JPanel operation = new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        operation.setSize(400,100);
        operation.add(start);
        operation.add(stop);
        operation.add(sava);
        sava.setEnabled(false);
        stop.setEnabled(false);
        operation.add(exit);
        start.addActionListener(this);
        stop.addActionListener(this);
        exit.addActionListener(this);
        sava.addActionListener(this);

        JLabel logLabel = new JLabel("服务器日志:");
        logLabel.setPreferredSize(new Dimension(400,30));


        logArea.setPreferredSize(new Dimension(400,470));

        right.add(operation);
        right.add(logLabel);
        right.add(logArea);
    }

}



