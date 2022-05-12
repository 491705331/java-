package Div;

import Ui.ChatRoomUi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * 群组列表组件
 */
public class GroupDiv extends JPanel {
    private final JButton button = new JButton();
    private int groupId;

    public GroupDiv(int groupId,String text,int width,int height){
        this.groupId = groupId;
        setPreferredSize(new Dimension(width,height));
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(Color.black);
        button.setText(text);
        button.setPreferredSize( new Dimension(width-60,height-10));
        button.setBackground(new Color(222, 220, 211));
        button.addActionListener(e->{
            ChatRoomUi.nowGroupId = groupId;
        });
        add(button);
    }

    public void addActionListener(ActionListener l) {
        button.addActionListener(l);
    }

    public int getGroupId() {
        return groupId;
    }
}
