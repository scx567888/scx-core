package cool.scx._core.notice;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Method;
import cool.scx.vo.Json;

import java.util.stream.Collectors;


/**
 * 通知公告 增删改查 controller
 *
 * @author 司昌旭
 * @version 1.0.10
 */
@ScxController
public class NoticeController {

    /**
     * 获取所有在线用户列表
     *
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(method = Method.POST)
    public Json getAllOnlineUser() {
        var s = ScxContext.getOnlineItemList().stream().filter(u -> u.username != null).map(u -> u.username).collect(Collectors.toList());
        return Json.ok().data("onlineUserList", s);
    }

}
