package cool.scx.service.uploadfile;

import cool.scx.annotation.ScxMapping;
import cool.scx.base.BaseService;
import cool.scx.base.Param;
import cool.scx.boot.ScxConfig;
import cool.scx.util.FileUtils;
import cool.scx.util.StringUtils;
import cool.scx.vo.Json;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UploadFileService extends BaseService<UploadFile> {

    @ScxMapping
    public Json deleteFile(Long id) {
        var result = true;
        UploadFile uploadFile = getById(id);
        if (uploadFile != null) {
            var b = FileUtils.deleteFileByPath(ScxConfig.uploadFilePath + uploadFile.filePath);
            //当文件成功删除后在删除 数据库记录
            result = b && deleteByIds(id) == 1;
        }
        return result ? Json.ok("success") : Json.ok("error");
    }

    @ScxMapping()
    public Json listFile(Map<String, Object> params) {
        var param = new Param<>(new UploadFile());
        var fileIds = params.get("fileIds");
        if (StringUtils.isNotEmpty(fileIds)) {
            param.whereSql = " id in (" + String.join(",", fileIds.toString().split(",")) + ")";
        } else {
            param.whereSql = " id = -1";
        }
        return Json.ok().items(list(param));
    }

}
