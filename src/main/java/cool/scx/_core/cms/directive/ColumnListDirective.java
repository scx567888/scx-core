package cool.scx._core.cms.directive;

import cool.scx._core.cms.ColumnService;
import cool.scx.annotation.ScxTemplateDirective;
import cool.scx.base.BaseTemplateDirective;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxTemplateDirective
public class ColumnListDirective extends BaseTemplateDirective {

    private final ColumnService columnService;

    /**
     * <p>Constructor for ColumnListDirective.</p>
     *
     * @param columnService a {@link cool.scx._core.cms.ColumnService} object.
     */
    public ColumnListDirective(ColumnService columnService) {
        this.columnService = columnService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public Object getResults(Map<String, Object> params) {
        return columnService.listAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String directiveName() {
        return "column_list_tag";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String variableName() {
        return "column_list";
    }
}