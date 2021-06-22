package cool.scx._core._base.uploadfile;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.QueryParam;
import cool.scx.enumeration.OrderByType;

/**
 * UploadFileService
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class UploadFileService extends BaseService<UploadFile> {
    /**
     * 根据 md5 查找文件
     *
     * @param fileMD5 a {@link java.lang.String} object.
     * @return a {@link cool.scx._core._base.uploadfile.UploadFile} object.
     */
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new QueryParam().addOrderBy("uploadTime", OrderByType.DESC);
//        p.o.fileMD5 = fileMD5;
        return get(p);
    }

}
