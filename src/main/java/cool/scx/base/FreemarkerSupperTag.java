package cool.scx.base;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;

/**
 * Freemarker 标签父类
 * <p>
 * 让子类实现后，利用模版设计模式，委派给子类
 */

public interface FreemarkerSupperTag extends TemplateDirectiveModel {

    @Override
    default void execute(Environment environment, Map params, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
        Object paramWrap = getParams(params);
        var builder = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        TemplateModel templateModel = builder.build().wrap(paramWrap);
        environment.setVariable(getVariable(), templateModel);
        body.render(environment.getOut());
    }


    String getVariable();

    /**
     * 委派下去让子类实现，并且返回加工后的返回值
     * 可返回业务对象，或者集合
     *
     * @param params r
     * @return r
     */
    Object getParams(Map params);

    /**
     * 获取 long 参数
     */
    default Long getLong(Map params, String key) {
        return Long.valueOf(getString(params, key));
    }

    /**
     * 获取 String 参数
     */
    default String getString(Map params, String key) {
        Object element = params.get(key);
        String value;
        if (element instanceof SimpleScalar) {
            value = ((SimpleScalar) element).getAsString();
        } else {
            value = element.toString();
        }
        return value;
    }

    /**
     * 获取 int 参数
     */
    default Integer getInt(Map params, String key) {
        return Integer.valueOf(getString(params, key));
    }
}
