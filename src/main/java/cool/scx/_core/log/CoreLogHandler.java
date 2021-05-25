package cool.scx._core.log;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseLogHandler;

/**
 * <p>CoreLogHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
@ScxService
public class CoreLogHandler implements BaseLogHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordLog(String title, String content, String username, String userIp, Integer type) {

    }
}
