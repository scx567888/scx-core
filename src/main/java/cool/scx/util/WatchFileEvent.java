package cool.scx.util;

import java.nio.file.Path;

/**
 * 监听文件的事件类
 *
 * @author scx567888
 * @version 1.3.0
 */
public interface WatchFileEvent {

    /**
     * 创建文件时
     *
     * @param path 路径
     */
    default void onCreate(Path path) {
    }

    /**
     * 更新文件时 ,(包括重命名)
     *
     * @param path 路径
     */
    default void onModify(Path path) {
    }

    /**
     * 删除文件时
     *
     * @param path 路径
     */
    default void onDelete(Path path) {
    }

}
