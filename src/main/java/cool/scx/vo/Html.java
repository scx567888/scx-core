package cool.scx.vo;


import cool.scx.boot.ScxCmsConfig;
import cool.scx.boot.ScxConfig;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public final class Html {

    private final String pagePath;

    private final Map<String, Object> dataMap = new HashMap<>();

    public Html(String pagePath) {
        this.pagePath = pagePath;
    }

    public Html add(String key, Object value) {
        this.dataMap.put(key, value);
        return this;
    }

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
