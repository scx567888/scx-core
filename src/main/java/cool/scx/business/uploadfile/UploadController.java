package cool.scx.business.uploadfile;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Param;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Method;
import cool.scx.util.StringUtils;
import cool.scx.util.file.FileUtils;
import cool.scx.vo.Json;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>UploadController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("/api/uploadFile")
public class UploadController {

    private final UploadFileService uploadFileService;

    /**
     * <p>Constructor for UploadController.</p>
     *
     * @param uploadFileService a {@link cool.scx.business.uploadfile.UploadFileService} object.
     */
    public UploadController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    /**
     * <p>listFile.</p>
     *
     * @param fileIds a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "listFile", method = Method.POST)
    public Json listFile(String fileIds) {
        var param = new Param<>(new UploadFile());
        if (StringUtils.isNotEmpty(fileIds)) {
            String collect = Stream.of(fileIds.split(",")).map(s -> "'" + s + "'").collect(Collectors.joining(","));
            param.whereSql = " file_id in (" + collect + ")";
        } else {
            param.whereSql = " file_id = -1";
        }
        return Json.ok().items(uploadFileService.list(param));
    }


    /**
     * <p>deleteFile.</p>
     *
     * @param fileIds a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(value = "deleteFile", method = Method.DELETE)
    public Json deleteFile(String fileIds) {
        //先获取文件的基本信息
        var param = new Param<>(new UploadFile());
        param.queryObject.fileId = fileIds;
        UploadFile needDeleteFile = uploadFileService.get(param);

        //判断文件是否被其他人引用过
        var param1 = new Param<>(new UploadFile());
        param1.queryObject.fileMD5 = needDeleteFile.fileMD5;
        Integer count = uploadFileService.count(param1);

        //删除数据库中的文件数据
        uploadFileService.deleteByIds(needDeleteFile.id);
        //没有被其他人引用过 可以删除物理文件
        if (count == 1) {
            FileUtils.deleteFiles(Path.of(ScxConfig.uploadFilePath() + "\\" + needDeleteFile.filePath).getParent());
        }

        return Json.ok("deleteSuccess");
    }


}
