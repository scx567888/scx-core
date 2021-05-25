package cool.scx.boot;

import cool.scx.context.LoginItem;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.*;
import java.util.List;

/**
 * <p>ScxListener class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxListener {
    static {
        try {
            var fis = new FileInputStream(FileUtils.getFileByRootModulePath("scx-session.cache"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            var o = (List<LoginItem>) objectInputStream.readObject();
//            for (var entry : o.entrySet()) {
////                ScxContext.addLoginItem(entry.getKey(), entry.getValue());
//            }
            objectInputStream.close();
        } catch (Exception ignored) {

        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Ansi.OUT.red("项目停止!!!");
                var fos = new FileOutputStream(FileUtils.getFileByRootModulePath("scx-session.cache"));
                var objectOutputStream = new ObjectOutputStream(fos);
                objectOutputStream.writeObject(ScxContext.getAllLoginItem());
                objectOutputStream.close();

            } catch (IOException ignored) {

            }
        }));
    }

    /**
     * 初始化 事件监听
     */
    public static void initListener() {

    }
}
