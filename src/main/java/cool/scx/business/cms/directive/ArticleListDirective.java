package cool.scx.business.cms.directive;


import cool.scx.base.FreemarkerSupperTag;
import cool.scx.business.cms.ArticleService;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class ArticleListDirective implements FreemarkerSupperTag {

    ArticleService articleService = new ArticleService();

    /** {@inheritDoc} */
    @Override
    public Object getParams(Map params) {
        return articleService.listMapAll();
    }

    /** {@inheritDoc} */
    @Override
    public String getVariable() {
        return "article_list";
    }

}
