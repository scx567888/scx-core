package cool.scx._ext.fss;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;
import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;

/**
 * UploadFileService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class UploadFileService extends BaseService<UploadFile> {

    /**
     * 根据 md5 查找文件
     *
     * @param fileMD5 md5 值
     * @return 找的的数据
     */
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new Query()
                .addOrderBy("uploadTime", OrderByType.DESC)
                .addWhere("fileMD5", WhereType.EQUAL, fileMD5);
        return get(p);
    }

}
