package cool.scx.business.notice;

import cool.scx.annotation.http.ScxController;
import cool.scx.annotation.http.ScxMapping;
import cool.scx.enumeration.RequestMethod;
import cool.scx.vo.Json;

import java.util.stream.Collectors;


/**
 * 通知公告 增删改查 controller
 */
@ScxController
public class NoticeController {

    @ScxMapping(method = RequestMethod.POST)
    public Json getAllOnlineUser() {
        var s = NoticeWebSocketController.WEB_SOCKET_SESSIONS.stream().filter(u ->
                u.user != null).collect(Collectors.toList());
        return Json.ok().data("onlineUserList", s);
    }
}
