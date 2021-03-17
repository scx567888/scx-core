package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.config.ScxCmsConfig;
import cool.scx.config.ScxConfig;
import freemarker.template.Template;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * html 渲染类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class Html implements BaseVo {

    private final Template template;

    private final Map<String, Object> dataMap = new HashMap<>();

    /**
     * 构造函数
     *
     * @param pagePath 模板的路径
     */
    public Html(String pagePath) {
        template = getTemplateByPath(pagePath);
    }

    private static Template getTemplateByPath(String pagePath) {
        try {
            return ScxCmsConfig.freemarkerConfig.getTemplate(pagePath + ScxConfig.cmsTemplateSuffix());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>add.</p>
     *
     * @param key   a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link cool.scx.vo.Html} object.
     */
    public Html add(String key, Object value) {
        dataMap.put(key, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void sendToClient(RoutingContext context) {
        var sw = new StringWriter();
        try {
            template.process(dataMap, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        var response = context.response();
        response.putHeader("Content-Type", "text/html; charset=utf-8");
        response.end(Buffer.buffer(sw.toString()));
    }
}
