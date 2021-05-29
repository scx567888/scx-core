package cool.scx.vo;

import cool.scx.base.BaseVo;
import io.vertx.ext.web.RoutingContext;

/**
 * 二进制文件 但不需要下载的 vo
 * 比如 pdf 之类
 * todo
 *
 * @author 司昌旭
 * @version 0.7.0
 */
public class Binary implements BaseVo {

    /**
     * {@inheritDoc}
     * <p>
     * sendToClient
     */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {

    }
}
