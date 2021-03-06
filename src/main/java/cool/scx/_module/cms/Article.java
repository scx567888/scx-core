package cool.scx._module.cms;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 文章类
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "cms")
public class Article extends BaseModel {

    /**
     * 文章标题
     */
    public String articleTitle;

    /**
     * 文章内容
     */
    @Column(type = "TEXT")
    public String articleContent;

    /**
     * 对应的 栏目 id
     */
    @Column(notNull = true)
    public Integer columnId;

}
