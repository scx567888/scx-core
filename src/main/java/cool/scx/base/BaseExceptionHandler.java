package cool.scx.base;

import javax.swing.*;

public abstract class BaseExceptionHandler {

    static {
        try {
            // 初始化提示框样式
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}