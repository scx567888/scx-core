package cool.scx.eventbus;

/**
 * 像前端发送的信息的封装对象
 *
 * @author 司昌旭
 * @version 1.1.17
 */
public class ScxWebSocketEventResult {
    public String callBackID;
    public Object data;

    /**
     * <p>Constructor for ScxWebSocketEventResult.</p>
     *
     * @param callBackID a {@link java.lang.String} object
     * @param data       a {@link java.lang.Object} object
     */
    public ScxWebSocketEventResult(String callBackID, Object data) {
        this.callBackID = callBackID;
        this.data = data;
    }
}
