package cool.scx.gui;

import javax.swing.*;

/**
 * 基本 GUI 父类
 * 此处只做 UI 框架的初始化
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public abstract class BaseGUIHandler {

    static {
        try {
            // 初始化提示框样式
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
