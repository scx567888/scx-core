package cool.scx._test;

import cool.scx.BaseModule;
import cool.scx.ScxApp;
import cool.scx.ScxEventBus;
import cool.scx._ext.cms.CmsModule;
import cool.scx._ext.core.CoreModule;
import cool.scx._ext.crud.CrudModule;
import cool.scx._ext.media.MediaModule;
import cool.scx._ext.message.MessageModule;
import cool.scx._ext.office.OfficeModule;
import cool.scx._ext.organization.OrganizationModule;
import cool.scx._ext.pay.PayModule;
import cool.scx._ext.fss.UploadModule;
import cool.scx._test0.Test0Module;
import cool.scx.bo.WSBody;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;

import java.time.LocalDateTime;

public class TestModule implements BaseModule {

    public static void main(String[] args) {
        //引入模块
        BaseModule[] modules = {
                new CoreModule(),
                new CmsModule(),
                new CrudModule(),
                new MediaModule(),
                new MessageModule(),
                new OfficeModule(),
                new OrganizationModule(),
                new PayModule(),
                new UploadModule(),
                new Test0Module(),
                new TestModule(),
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
        ScxEventBus.consumer("sendMessage", (m) -> SendMessageHandler.sendMessage((WSBody) m));

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
