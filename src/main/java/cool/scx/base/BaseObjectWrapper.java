package cool.scx.base;

import cool.scx.util.object.ObjectUtils;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

/**
 * 自定义的 freemarker  ObjectWrapper
 *
 * @author scx56
 * @version $Id: $Id
 */
public class BaseObjectWrapper extends DefaultObjectWrapper {

    /**
     * <p>Constructor for BaseObjectWrapper.</p>
     *
     * @param incompatibleImprovements a {@link freemarker.template.Version} object.
     */
    public BaseObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    /**
     * {@inheritDoc}
     */
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        return super.wrap(ObjectUtils.objectToMapDeep(obj));
    }

}
