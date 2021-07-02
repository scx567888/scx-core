package cool.scx.vo;

import cool.scx.template.ScxTemplate;
import freemarker.template.Template;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * html 渲染类
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class Html implements BaseVo {

    private final Template template;

    private final String htmlStr;

    private final Map<String, Object> dataMap = new HashMap<>();

    private Html(Template template, String htmlStr) {
        this.template = template;
        this.htmlStr = htmlStr;
    }

    /**
     * <p>ofString.</p>
     *
     * @param htmlStr a {@link java.lang.String} object
     * @return a {@link cool.scx.vo.Html} object
     */
    public static Html ofString(String htmlStr) {
        return new Html(null, htmlStr);
    }

    /**
     * <p>ofTemplate.</p>
     *
     * @param templatePath a {@link java.lang.String} object
     * @return a {@link cool.scx.vo.Html} object
     */
    public static Html ofTemplate(String templatePath) {
        var template = ScxTemplate.getTemplateByPath(templatePath);
        return new Html(template, null);
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

    /**
     * {@inheritDoc}
     * <p>
     * sendToClient
     */
    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        response.putHeader("Content-Type", "text/html; charset=utf-8");
        if (template != null) {
            sendTemplate(response);
        } else {
            sendStr(response);
        }
    }

    private void sendStr(HttpServerResponse response) {
        response.end(Buffer.buffer(htmlStr));
    }

    private void sendTemplate(HttpServerResponse response) {
        var sw = new StringWriter();
        try {
            template.process(dataMap, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.end(Buffer.buffer(sw.toString()));
    }

}
