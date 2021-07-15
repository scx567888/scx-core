package cool.scx._ext;

import cool.scx.BaseModule;
import cool.scx.ScxApp;
import cool.scx._ext.cms.CmsModule;
import cool.scx._ext.core.CoreModule;
import cool.scx._ext.crud.CrudModule;
import cool.scx._ext.media.MediaModule;
import cool.scx._ext.message.MessageModule;
import cool.scx._ext.office.OfficeModule;
import cool.scx._ext.organization.OrganizationModule;
import cool.scx._ext.pay.PayModule;
import cool.scx._ext.upload.UploadModule;

/**
 * 运行所有核心包提供的模块 (演示用,不要用于生产环境)
 *
 * @author scx567888
 * @version 1.1.11
 */
public class AllExtApp {


    /**
     * 核心启动方法
     *
     * @param args 外部参数
     */
    public static void main(String[] args) {
        BaseModule[] modules = {
                new CoreModule(),
                new CmsModule(),
                new CrudModule(),
                new MediaModule(),
                new MessageModule(),
                new OfficeModule(),
                new OrganizationModule(),
                new PayModule(),
                new UploadModule()
        };
        ScxApp.run(modules, args);
    }

}
