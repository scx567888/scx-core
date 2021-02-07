package cool.scx.vo;


import cool.scx.boot.ScxCmsConfig;
import cool.scx.boot.ScxConfig;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Html class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class Html {

    private final String pagePath;

    private final Map<String, Object> dataMap = new HashMap<>();

    /**
     * <p>Constructor for Html.</p>
     *
     * @param pagePath a {@link java.lang.String} object.
     */
    public Html(String pagePath) {
        this.pagePath = pagePath;
    }

    /**
     * <p>add.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link cool.scx.vo.Html} object.
     */
    public Html add(String key, Object value) {
        this.dataMap.put(key, value);
        return this;
    }

    /**
     * <p>getHtmlStr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHtmlStr() {
        StringWriter sw = new StringWriter();
        try {
            var template = ScxCmsConfig.freemarkerConfig.getTemplate(pagePath + ScxConfig.cmsResourceSuffix);
            template.process(this.dataMap, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
}
