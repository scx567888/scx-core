package cool.scx.core.system;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 日志记录表
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class ScxLog extends BaseModel {

    public String userIp;//用户ip

    /**
     * 日志类型  目前分为两种 一种是 系统信息 如 非法登录 内存溢出 文件下载等 标识符 为  0
     * 另一种是 业务日志  如 某某某 在 2019-01-01 删除了 xxx 数据等
     */
    public Integer type;

    public String username;//操作人 姓名

    public String title;//事件 title

    public String content;//事件 内容

}
