package cool.scx._core.cms;

import cool.scx.BaseModel;
import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;

/**
 * 文章类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "core")
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
