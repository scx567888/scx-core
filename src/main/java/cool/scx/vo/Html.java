package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.boot.ScxCmsConfig;
import cool.scx.boot.ScxConfig;
import cool.scx.util.ObjectUtils;
import freemarker.template.Template;
import io.vertx.core.buffer.Buffer;

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
            return ScxCmsConfig.freemarkerConfig.getTemplate(pagePath + ScxConfig.cmsResourceSuffix);
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
        dataMap.put(key, ObjectUtils.objectToMapDeep(value));
        return this;
    }


    /**
     * {@inheritDoc}
     * <p>
     * 根据 dataMap 利用 freemarker 进行渲染
     */
    @Override
    public Buffer getBuffer() {
        var sw = new StringWriter();
        try {
            template.process(dataMap, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Buffer.buffer(sw.toString());
    }

    @Override
    public String getContentType() {
        return "text/html; charset=utf-8";
    }

    @Override
    public String getContentDisposition() {
        return null;
    }
}
