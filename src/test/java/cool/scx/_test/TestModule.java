package cool.scx._test;

import cool.scx.ScxApp;
import cool.scx.ScxModule;
import cool.scx._module.auth.AuthModule;
import cool.scx._module.base.BaseModule;
import cool.scx._module.cms.CmsModule;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.eventbus.ScxEventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

public class TestModule implements ScxModule {

    public static void main(String[] args) {
        //引入模块
        ScxModule[] modules = {
                new BaseModule(),
                new CmsModule(),
                new AuthModule(),
                new TestModule()
        };
        //运行项目
        ScxApp.run(modules, args);
    }

    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

    @Override
    public void start() {
        //注册事件
        ScxEventBus.consumer("sendMessage", (Message<JsonObject> m) -> SendMessageHandler.sendMessage(m.body()));

        while (true) {
            var onlineItemList = ScxContext.getOnlineItemList();
            for (var onlineItem : onlineItemList) {
                onlineItem.send("writeTime", ScxConfig.DATETIME_FORMATTER.format(LocalDateTime.now()));
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
        }

    }
}
