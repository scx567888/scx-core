package cool.scx._core.log;

import cool.scx.BaseModel;
import cool.scx.annotation.ScxModel;

/**
 * 日志记录表
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
public class Log extends BaseModel {

    /**
     * 用户ip
     */
    public String userIp;//

    /**
     * 日志类型  目前分为两种 一种是 系统信息 如 非法登录 内存溢出 文件下载等 标识符 为  0
     * 另一种是 业务日志  如 某某某 在 2019-01-01 删除了 xxx 数据等
     */
    public Integer type;

    /**
     * 操作人 姓名
     */
    public String username;

    /**
     * 事件 title
     */
    public String title;

    /**
     * 事件 内容
     */
    public String content;

}
