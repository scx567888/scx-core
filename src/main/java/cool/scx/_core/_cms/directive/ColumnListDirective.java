package cool.scx._core._cms.directive;

import cool.scx._core._cms.column.ColumnService;
import cool.scx.annotation.ScxTemplateDirective;
import cool.scx.cms.BaseTemplateDirective;

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
     * @param columnService a {@link cool.scx._core._cms.column.ColumnService} object.
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
     * <p>
     * directiveName
     */
    @Override
    public String directiveName() {
        return "column_list_tag";
    }

    /**
     * {@inheritDoc}
     * <p>
     * directiveName
     */
    @Override
    public String variableName() {
        return "column_list";
    }
}
