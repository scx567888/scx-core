package cool.scx.base;

import javax.swing.*;

/**
 * BaseExceptionHandler 父类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
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
