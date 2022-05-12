package Div;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 服务端操作按钮组件
 */
public class OptionBtn extends JButton {
    public OptionBtn(String title){
        super(title);
        this.setBackground(Color.WHITE);
        this.setMargin(new Insets(10,20,10,20));
        this.setPreferredSize(new Dimension(80,30));
        this.setBorder(new LineBorder(Color.black));
    }
}
