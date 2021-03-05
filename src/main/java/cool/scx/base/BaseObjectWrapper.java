package cool.scx.base;

import cool.scx.util.object.ObjectUtils;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

/**
 * 自定义的 freemarker  ObjectWrapper
 */
public class BaseObjectWrapper extends DefaultObjectWrapper {

    public BaseObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    public TemplateModel wrap(Object obj) throws TemplateModelException {
        return super.wrap(ObjectUtils.objectToMapDeep(obj));
    }

}
