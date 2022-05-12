package Div;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 聊天会话组件，根据qqNum是否为自己显示头像在右侧还是左侧
 */
public class ChatContent extends JPanel {

    private String qqNum;

    public ChatContent(String meg, String qqNum, Boolean isMyMeg, int width) {
        this.qqNum = qqNum;
        ImageIcon headerImg = null;
        try {
            headerImg = new ImageIcon(new URL("https://q.qlogo.cn/g?b=qq&nk="+qqNum+"&s=100"));
        } catch (MalformedURLException ignored) {
        }
        headerImg.setImage(headerImg.getImage().getScaledInstance(35, 35,4 ));
        JPanel header = new JPanel(); //头像
        JLabel label = new JLabel(headerImg);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,"Ta的qq号为："+qqNum,"信息",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        header.add(label);
        setPreferredSize(new Dimension(width,45));
        JPanel content = new JPanel(); //聊天内容
        JLabel text = new JLabel(meg);
        text.setFont(new Font(null, Font.PLAIN,16));
        content.add( text );
        if (isMyMeg){ //是我发送的聊天内容
            setLayout(new FlowLayout(FlowLayout.RIGHT,5,0));
            content.setBackground(Color.green);
            add(content);
            add(header);
        } else { //他人的聊天内容
            setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
            content.setBackground(new Color(168, 167, 167));
            add(header);
            add(content);
        }
    }
}
