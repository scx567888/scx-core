package cool.scx.vo;

import cool.scx.util.FileTypeUtils;
import cool.scx.util.FixedMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.AbsoluteSize;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Image class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class Image implements BaseVo {

    private static final FixedMap<String, Buffer> imageCache = new FixedMap<>(100);
    /**
     * type 和裁剪类型 映射表
     */
    private static final Map<String, Positions> TYPE_POSITIONS_MAP = new HashMap<>();

    static {
        TYPE_POSITIONS_MAP.put("top-left", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("top-center", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("top-right", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("center-left", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-right", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bottom-left", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bottom-center", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("bottom-right", Positions.BOTTOM_RIGHT);
        //简写
        TYPE_POSITIONS_MAP.put("tl", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("tc", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("tr", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("cl", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("c", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cc", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cr", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bl", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bc", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("br", Positions.BOTTOM_RIGHT);
    }

    private final File file;
    /**
     * 裁剪的宽度
     */
    private final Integer width;
    /**
     * 裁剪的高度
     */
    private final Integer height;
    /**
     * 裁剪的类型 注意!!! 只有设置 width 和 height 时生效
     * 类型及简写请参照 TYPE_POSITIONS_MAP
     * TYPE_POSITIONS_MAP
     */
    private final String type;


    /**
     * <p>Constructor for Image.</p>
     *
     * @param _file a {@link java.io.File} object.
     */
    public Image(File _file) {
        file = _file;
        width = null;
        height = null;
        type = "z";
    }

    /**
     * <p>Constructor for Image.</p>
     *
     * @param _file   a {@link java.io.File} object.
     * @param _width  a {@link java.lang.Integer} object.
     * @param _height a {@link java.lang.Integer} object.
     * @param _type   a {@link java.lang.String} object
     */
    public Image(File _file, Integer _width, Integer _height, String _type) {
        file = _file;
        width = _width;
        height = _height;
        type = _type == null ? "z" : _type;
    }

    /**
     * <p>Constructor for Image.</p>
     *
     * @param _filePath a {@link java.lang.String} object.
     */
    public Image(String _filePath) {
        file = new File(_filePath);
        width = null;
        height = null;
        type = "z";
    }

    /**
     * <p>Constructor for Image.</p>
     *
     * @param _filePath a {@link java.lang.String} object.
     * @param _width    a {@link java.lang.Integer} object.
     * @param _height   a {@link java.lang.Integer} object.
     * @param _type     a {@link java.lang.String} object
     */
    public Image(String _filePath, Integer _width, Integer _height, String _type) {
        file = new File(_filePath);
        width = _width;
        height = _height;
        type = _type == null ? "z" : _type;
    }

    /**
     * <p>cleanCache.</p>
     */
    public static void cleanCache() {
        imageCache.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * sendToClient
     */
    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        //设置缓存 减少服务器压力
        response.putHeader("cache-control", "public,immutable,max-age=2628000");
        response.putHeader("accept-ranges", "bytes");
        boolean b = checkImageCache(response);
        if (b) {
            return;
        }
        // 图片不存在 这里抛出不存在异常
        if (!file.exists()) {
            notFound(response);
            return;
        }
        var imageFileType = FileTypeUtils.getFileTypeForFile(file);
        if (imageFileType != null && imageFileType.isImage) {
            response.putHeader("content-type", imageFileType.mimeType);
            if (height == null && width == null) {
                sendRawPicture(response);
            } else {
                sendCroppedPicture(response);
            }
        } else {
            response.putHeader("content-type", "image/png");
            sendSystemIcon(response);
        }
    }

    /**
     * 检查图片缓存
     *
     * @param response r
     * @return r
     */
    private boolean checkImageCache(HttpServerResponse response) {
        var str = file.getPath() + ";" + height + ";" + width + ";" + type;
        var buffer = imageCache.get(str);
        if (buffer != null) {
            response.end(buffer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 就不是普通的图片 我们就返回他在操作系统中的展示图标即可
     *
     * @param response r
     */
    private void sendSystemIcon(HttpServerResponse response) {
        try (var out = new ByteArrayOutputStream()) {
            var image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
            var myImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            var g = myImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            ImageIO.write(myImage, "png", out);
            var b = Buffer.buffer(out.toByteArray());
            imageCache.put(file.getPath() + ";" + height + ";" + width, b);
            response.end(b);
        } catch (Exception e) {
            notFound(response);
        }
    }

    /**
     * 没找到图片
     *
     * @param response r
     */
    private void notFound(HttpServerResponse response) {
        response.setStatusCode(404).end("No Found");
    }

    /**
     * 裁剪后的图片
     *
     * @param response r
     */
    private void sendCroppedPicture(HttpServerResponse response) {
        try (var out = new ByteArrayOutputStream()) {
            var image = Thumbnails.of(file).scale(1.0).asBufferedImage();
            var imageHeight = image.getHeight();
            var imageWidth = image.getWidth();

            var croppedHeight = (height == null || height > imageHeight || height <= 0) ? imageHeight : height;
            var croppedWidth = (width == null || width > imageHeight || width <= 0) ? imageWidth : width;

            AbsoluteSize absoluteSize = new AbsoluteSize(croppedWidth, croppedHeight);
            Positions positions = TYPE_POSITIONS_MAP.get(type.toLowerCase());
            if (positions != null) {
                Thumbnails.of(file).sourceRegion(positions, absoluteSize).size(croppedWidth, croppedHeight).keepAspectRatio(false).toOutputStream(out);
            } else {
                Thumbnails.of(file).size(croppedWidth, croppedHeight).keepAspectRatio(false).toOutputStream(out);
            }

            Buffer b = Buffer.buffer(out.toByteArray());
            imageCache.put(file.getPath() + ";" + height + ";" + width + ";" + type, b);
            response.end(b);
        } catch (Exception e) {
            notFound(response);
        }
    }

    /**
     * 发送原始图片
     *
     * @param response r
     */
    private void sendRawPicture(HttpServerResponse response) {
        // 没有宽高 直接返回图片本身
        try (var input = new FileInputStream(file)) {
            byte[] byt = new byte[input.available()];
            int read = input.read(byt);
            Buffer b = Buffer.buffer(byt);
            imageCache.put(file.getPath() + ";" + height + ";" + width, b);
            response.end(b);
            byt = null;
        } catch (Exception e) {
            notFound(response);
        }
    }
}
