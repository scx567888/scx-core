package cool.scx.core.uploadfile;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.enumeration.SortType;

/**
 * <p>UploadFileService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class UploadFileService extends BaseService<UploadFile> {
    /**
     * <p>findFileByMd5.</p>
     *
     * @param fileMD5 a {@link java.lang.String} object.
     * @return a {@link cool.scx.core.uploadfile.UploadFile} object.
     */
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new Param<>(new UploadFile()).addOrderBy("uploadTime", SortType.DESC);
        p.queryObject.fileMD5 = fileMD5;
        return get(p);
    }
}
