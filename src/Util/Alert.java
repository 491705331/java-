package Util;

import javax.swing.*;

/**
 * gui弹窗工具类
 */
public class Alert {
    public static void error(String msg){ //提示错误信息
        JOptionPane.showMessageDialog(null,msg,"错误",JOptionPane.ERROR_MESSAGE);
    }

    public static void info(String msg){ //提示信息
        JOptionPane.showMessageDialog(null,msg,"提示",JOptionPane.INFORMATION_MESSAGE);
    }
}
