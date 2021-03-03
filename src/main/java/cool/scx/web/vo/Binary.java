package cool.scx.web.vo;

import cool.scx.web.base.BaseVo;
import io.vertx.ext.web.RoutingContext;

/**
 * 二进制文件 但不需要下载的 vo
 * 比如 pdf 之类
 * todo
 *
 * @author scx56
 * @version 0.7.0
 */
public class Binary implements BaseVo {

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {

    }
}
