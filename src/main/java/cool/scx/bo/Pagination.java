package cool.scx.bo;

/**
 * 分页参数
 */
public class Pagination {

    /**
     * 分页 页码
     */
    private Integer page = 0;

    /**
     * 分页 每页数量
     */
    private Integer limit = 0;

    public Pagination() {

    }

    /**
     * 设置分页参数
     *
     * @param page  分页页码
     * @param limit 每页数量
     * @return p
     */
    public Pagination set(Integer page, Integer limit) {
        if (page >= 0 && limit >= 0) {
            this.page = page;
            this.limit = limit;
            return this;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
    }

    /**
     * 设置分页 默认 第一页
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a 当前实例
     */
    public Pagination set(Integer limit) {
        if (limit >= 0) {
            this.page = 1;
            this.limit = limit;
            return this;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
    }

}
