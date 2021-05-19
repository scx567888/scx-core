package cool.scx.base;

public interface BaseLogHandler {
    void recordLog(String title, String content, String username, String userIp, Integer type);
}
