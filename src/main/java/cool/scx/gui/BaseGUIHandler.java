package cool.scx.gui;

import javax.swing.*;

/**
 * GUI 模块 的父类
 * 只用作 UI 框架的初始化
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
