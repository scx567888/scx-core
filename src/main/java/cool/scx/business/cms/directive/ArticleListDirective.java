package cool.scx.business.cms.directive;


import cool.scx.base.FreemarkerSupperTag;
import cool.scx.business.cms.ArticleService;

import java.util.Map;

/**
 * 自定义标签测试
 */
public class ArticleListDirective implements FreemarkerSupperTag {

    ArticleService articleService = new ArticleService();

    @Override
    public Object getParams(Map params) {
        return articleService.listMapAll();
    }

    @Override
    public String getVariable() {
        return "article_list";
    }

}
