package cool.scx.business.notice;

import cool.scx.web.annotation.ScxController;
import cool.scx.web.annotation.ScxMapping;
import cool.scx.context.ScxContext;
import cool.scx.web.type.RequestMethod;
import cool.scx.web.vo.Json;

import java.util.stream.Collectors;


/**
 * 通知公告 增删改查 controller
 *
 * @author scx56
 * @version $Id: $Id
 */
@ScxController
public class NoticeController {

    /**
     * <p>getAllOnlineUser.</p>
     *
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping(method = RequestMethod.POST)
    public Json getAllOnlineUser() {
        var s = ScxContext.getOnlineItemList().stream().filter(u -> u.username != null).map(u -> u.username).collect(Collectors.toList());
        return Json.ok().data("onlineUserList", s);
    }
}
