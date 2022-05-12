package Div;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 在线列表用户组件
 */
public class UserDiv extends JPanel {
    public UserDiv(String username,String qqNum,int width,int height){
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setPreferredSize(new Dimension(width,height));
        setBackground(Color.black);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,"Ta的qq号为："+qqNum,"信息",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setPreferredSize(new Dimension(width-60,height));
        container.setBackground(new Color(68, 66, 66));
        ImageIcon headerImg = null;
        try {
            headerImg = new ImageIcon(new URL("https://q.qlogo.cn/g?b=qq&nk="+qqNum+"&s=100"));
        } catch (MalformedURLException ignored) {
        }
        headerImg.setImage(headerImg.getImage().getScaledInstance(height-15, height-15,4 ));
        JLabel label = new JLabel("<html> <b style='color:white;font-size:12px;'>&nbsp;&nbsp;"+username+"<b> </html>" , headerImg,JLabel.LEFT);
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setVerticalTextPosition(JLabel.CENTER);
        container.add(label);
        add(container);
    }
}
