package cool.scx.template;

import cool.scx.ScxEventBus;
import cool.scx.annotation.ScxTemplateDirective;
import cool.scx.base.BaseTemplateDirective;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModule;
import cool.scx.util.Ansi;
import cool.scx.util.ScxUtils;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.Version;

import java.io.IOException;
import java.util.List;

/**
 * ScxTemplate
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class ScxTemplate {

    /**
     * Freemarker 默认引擎版本
     */
    private static final Version VERSION = Configuration.VERSION_2_3_31;

    /**
     * Constant <code>freemarkerConfig</code>
     */
    private static final Configuration freemarkerConfig = initFreemarkerConfig();

    static {
        //Bean 加载完毕后的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REGISTER_NAME, o -> {
            var scxModuleList = ScxUtils.cast(o);
            addTemplateDirective(scxModuleList);
        });

        //Bean 销毁时的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REMOVE_NAME, scxModule -> {

        });
    }


    private static Configuration initFreemarkerConfig() {
        // freemarker 配置文件版本
        var configuration = new Configuration(VERSION);
        var wrapperBuilder = new DefaultObjectWrapperBuilder(VERSION);
        //暴露 实体类的 fields 因为 此项目中的实体类没有 get set
        wrapperBuilder.setExposeFields(true);
        configuration.setObjectWrapper(wrapperBuilder.build());
        try {
            configuration.setDirectoryForTemplateLoading(ScxConfig.templateRoot());
        } catch (Exception e) {
            Ansi.out().brightRed(ScxConfig.templateRoot().getPath()).println();
            Ansi.out().brightRed("模板目录不存在!!!").println();
        }

        //设置 字符集
        configuration.setDefaultEncoding("UTF-8");
        //设置 语法 为自动检测
        configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);

        return configuration;
    }

    //自定义的指令就在这里添加
    public static void addTemplateDirective(List<ScxModule> scxModuleList) {
        for (ScxModule scxModule : scxModuleList) {
            for (Class<?> clazz : scxModule.classList) {
                if (clazz.isAnnotationPresent(ScxTemplateDirective.class) && BaseTemplateDirective.class.isAssignableFrom(clazz)) {
                    try {
                        var myDirective = (BaseTemplateDirective) ScxContext.getBean(clazz);
                        Ansi.out().blue("已加载自定义 Freemarker 标签 [" + myDirective._DirectiveName() + "] Class -> " + clazz.getName()).println();
                        freemarkerConfig.setSharedVariable(myDirective._DirectiveName(), myDirective);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 初始化 cms 配置文件
     */
    public static void initTemplate() {
        Ansi.out().blue("ScxTemplate 初始化完成...").println();
    }

    /**
     * <p>getTemplateByPath.</p>
     *
     * @param pagePath a {@link java.lang.String} object.
     * @return a {@link freemarker.template.Template} object.
     */
    public static Template getTemplateByPath(String pagePath) {
        try {
            return freemarkerConfig.getTemplate(pagePath + ".html");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
