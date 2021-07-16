package cool.scx.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * <p>WatchFileUtils class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public final class WatchFileUtils {

    /**
     * 监听文件夹 ,(只监听一层)
     *
     * @param watchPath      监听的路径
     * @param watchFileEvent 监听执行事件
     */
    public static void watchDir(Path watchPath, WatchFileEvent watchFileEvent) {
        try {
            new WatchFileTask(watchPath, watchFileEvent).processEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听任务
     */
    private static class WatchFileTask {

        /**
         * 因各个操作系统平台有差异 所以这里获取系统默认的 WatchService
         */
        private final WatchService watcher = FileSystems.getDefault().newWatchService();

        /**
         * a
         */
        private final Map<WatchKey, Path> keys = new HashMap<>();

        /**
         * 监听的事件
         */
        private final WatchFileEvent watchFileEvent;

        /**
         * Creates a WatchService and registers the given directory
         */
        WatchFileTask(Path dir, WatchFileEvent watchFileEvent) throws IOException {
            this.watchFileEvent = watchFileEvent;
            keys.put(dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), dir);
        }

        /**
         * Process all events for keys queued to the watcher
         */
        void processEvents() {
            while (true) {
                //等待 key 发出信号
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }
                //获取事件的路径
                var dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (var event : key.pollEvents()) {
                    var kind = event.kind();
                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }
                    //获取事件发生的绝对路径
                    Path path = dir.resolve((Path) event.context());
                    // 创建事件
                    if (kind == ENTRY_CREATE) {
                        watchFileEvent.onCreate(path);
                    } else if (kind == ENTRY_DELETE) {
                        watchFileEvent.onDelete(path);
                    } else if (kind == ENTRY_MODIFY) {
                        watchFileEvent.onModify(path);
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

}
