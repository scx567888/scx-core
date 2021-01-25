package cool.scx.business.cms;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 文章类
 */
@ScxModel(tablePrefix = "cms")
public class Article extends BaseModel {

    public String articleTitle;//文章标题

    @Column(type = "TEXT")
    public String articleContent;//文章内容

    @Column(notNull = true)
    public Integer columnId;//对应的 栏目 id
}
