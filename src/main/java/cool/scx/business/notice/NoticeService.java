package cool.scx.business.notice;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ScxService
public class NoticeService extends BaseService<Notice> {

    @Override
    public Notice save(Notice notice) {
        var newId = super.save(notice);
        //发送给所有人 (未在线用户登录的时候进行通知)
        if (notice.sendType == 0) {

        }
        //只发送给在线的人
        if (notice.sendType == 1) {
            List<Long> collect = Stream.of(notice.userIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
//            IMService.sendByUserIds(collect, notice);
        }
        return newId;
    }
}
