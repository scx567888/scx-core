package cool.scx.base;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.Map;

/**
 * Freemarker 标签父类
 * <p>
 * 让子类实现后，利用模版设计模式，委派给子类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public abstract class BaseTemplateDirective implements TemplateDirectiveModel {


    /**
     * {@inheritDoc}
     * 执行方法
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment environment, Map params, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
        var results = getResults((Map<String, Object>) params);
        TemplateModel wrap = environment.getObjectWrapper().wrap(results);
        environment.setVariable(variableName(), wrap);
        body.render(environment.getOut());
    }

    /**
     * 获取自定义指令的名称
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String directiveName();

    /**
     * 获取自定义 变量的名称
     *
     * @return a
     */
    public abstract String variableName();

    /**
     * 委派下去让子类实现，并且返回加工后的返回值
     * 可返回业务对象，或者集合
     *
     * @param params r
     * @return r
     */
    public abstract Object getResults(Map<String, Object> params);

}
