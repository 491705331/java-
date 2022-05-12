package Div;

import javax.swing.*;
import java.awt.*;

public class TitleBar extends JPanel {
    public TitleBar(String title,int width,int height){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(width,height));
        setBackground(Color.BLACK);
        add( new JLabel("<html> <b style='color:rgb(153, 151, 151);font-size:10px;'>"+title+"<b> </html>")  );
    }

    public static void main(String[] args) {
        Color color = new Color(153, 151, 151);
    }
}
