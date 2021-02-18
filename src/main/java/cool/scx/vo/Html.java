package cool.scx.vo;

import cool.scx.base.BaseModel;
import cool.scx.boot.ScxCmsConfig;
import cool.scx.boot.ScxConfig;
import cool.scx.util.ObjectUtils;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * html 渲染类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class Html {

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
        Object stringObjectMap;
        if (value instanceof List) {
            stringObjectMap = ObjectUtils.beanListToMapList(value);
        } else if (value instanceof BaseModel) {
            stringObjectMap = ObjectUtils.beanToMap(value);
        } else {
            stringObjectMap = value;
        }
        dataMap.put(key, stringObjectMap);
        return this;
    }

    /**
     * 根据 dataMap 利用 freemarker 进行渲染
     *
     * @return 获取 html 字符串
     */
    public String getHtmlStr() {
        var sw = new StringWriter();
        try {
            template.process(dataMap, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
}
