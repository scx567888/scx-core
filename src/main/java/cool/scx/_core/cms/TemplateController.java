package cool.scx._core.cms;

import cool.scx.annotation.ScxMapping;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.Method;
import cool.scx.util.FileUtils;
import cool.scx.vo.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>TemplateController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxMapping
public class TemplateController {

    /**
     * 获取文件夹下的文件列表
     *
     * @param filePath 文件路径
     * @return 文件列表
     * @throws IOException if any.
     */
    private static List<FileInfo> getFileList(String filePath) throws IOException {
        var fileList = new LinkedList<FileInfo>();
        var path = Paths.get(filePath);
        Files.walkFileTree(path, new FileVisitor<>() {
            //访问文件夹之前自动调用此方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                var fileInfo = new FileInfo();
                fileInfo.type = "Directory";
                return getFileVisitResult(dir, fileInfo, path, fileList);
            }

            //访问文件时自动调用此方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                var fileInfo = new FileInfo();
                fileInfo.type = "File";
                return getFileVisitResult(file, fileInfo, path, fileList);
            }

            //访问文件失败时自动调用此方法
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            //访问文件夹之后自动调用此方法
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    private static FileVisitResult getFileVisitResult(Path file, FileInfo fileInfo, Path path, LinkedList<FileInfo> fileList) {
        fileInfo.id = file.getFileName().toString();
        fileInfo.parentId = file.getParent().toFile().getPath();
        if (path.toString().equals(fileInfo.parentId)) {
            fileInfo.parentId = "0";
        } else {
            fileInfo.parentId = file.getParent().getFileName().toString();
        }
        fileInfo.filePath = file.toFile().getPath();
        fileList.add(fileInfo);
        return FileVisitResult.CONTINUE;
    }

    /**
     * <p>Index.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping(method = {Method.GET, Method.POST})
    public Json Index() throws IOException {
        var fileList = getFileList(ScxConfig.cmsRoot().getPath());
        var collect = fileList.stream().filter(fileInfo -> "Directory".equals(fileInfo.type)).collect(Collectors.toList());
        var collect1 = fileList.stream().filter(fileInfo -> "File".equals(fileInfo.type)).collect(Collectors.toList());
        collect.addAll(collect1);
//        让文件夹永远在前边
        return Json.ok().data("cmsRootTreeList", collect);
    }

    /**
     * <p>getFileContent.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping("getFileContent")
    public Json getFileContent(Map<String, String> params) {
        String filePath = params.get("filePath");
        try {
            String fileContent = Files.readString(Paths.get(filePath));
            return Json.ok().data("fileContent", fileContent);
        } catch (Exception exception) {
            return Json.ok().data("fileContent", "此文件无法编辑");
        }
    }

    /**
     * <p>setFileContent.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping("setFileContent")
    public Json setFileContent(Map<String, String> params) {
        String filePath = params.get("filePath");
        String fileContent = params.get("fileContent");
//        注意 向文件写入字符串请用此方法 不要用 nio nio 有bug
        FileUtils.setFileContent(filePath, fileContent);
        return getFileContent(params);
    }

    /**
     * <p>delete.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping("file/delete")
    public Json delete(Map<String, String> params) throws IOException {
        String filePath = params.get("filePath");
        var file = Paths.get(filePath);
        FileUtils.deleteIfExists(file);
        return Json.ok();
    }

    /**
     * <p>upload.</p>
     *
     * @param file     a {@link java.io.File} object.
     * @param filePath a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping(value = "upload")
    public Json upload(File file, String filePath) throws IOException {
//        filePath = filePath + "\\" + file.getOriginalFilename();
//        FileUtils.fileAppend(filePath, file.getBytes());
        return Json.ok();
    }

    /**
     * <p>rename.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping("file/rename")
    public Json rename(Map<String, String> params) throws IOException {
        String newFilePath = params.get("newFilePath");
        String oldFilePath = params.get("oldFilePath");
        Path path = Paths.get(oldFilePath);
        String parent = path.getParent().toFile().getPath();
        path.toFile().renameTo(new File(parent + "\\" + newFilePath));
        return Json.ok();
    }

}
