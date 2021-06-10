package cool.scx.eventbus;

public class ScxWebSocketEventResult {
    public String callBackID;
    public Object data;

    public ScxWebSocketEventResult(String callBackID, Object data) {
        this.callBackID = callBackID;
        this.data = data;
    }
}
