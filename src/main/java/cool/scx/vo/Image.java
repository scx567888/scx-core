package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.util.FileType;

import cool.scx.util.FileUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Image implements BaseVo {

    private final File file;
    private Integer width;
    private Integer height;

    public Image(File _file) {
        file = _file;
        width = null;
        height = null;
    }

    public Image(File _file, Integer _width, Integer _height) {
        file = _file;
        width = _width;
        height = _height;
    }

    @Override
    public void sendToClient(RoutingContext context) {
        var response = context.response();
        response.putHeader("Cache-Control", "max-age=2628000");
        // 图片不存在 这里抛出不存在异常
        if (!file.exists()) {
            response.setStatusCode(404).end("No Found");
            return;
        }
        //设置缓存 减少服务器压力
        FileType imageFileType = FileUtils.getImageFileType(file);
        try (var out = new ByteArrayOutputStream()) {
            //就不是普通的图片 我们就返回他在操作系统中的展示图标即可
            if (imageFileType == null) {
                var image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
                var myImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                var g = myImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                ImageIO.write(myImage, "png", out);
                response.end(Buffer.buffer(out.toByteArray()));
            } else if (height == null && width == null) {
                // 没有宽高 直接返回图片本身
                try (var input = new FileInputStream(file)) {
                    byte[] byt = new byte[input.available()];
                    int read = input.read(byt);
                    response.end(Buffer.buffer(byt));
                    byt = null;
                }
            } else {
                // 有宽高 对图片进行裁剪
                var image = Thumbnails.of(file).scale(1.0).asBufferedImage();
                if (height == null || height > image.getHeight()) {
                    height = image.getHeight();
                }
                if (width == null || width > image.getWidth()) {
                    width = image.getWidth();
                }
                Thumbnails.of(file).size(width, height).keepAspectRatio(false).toOutputStream(out);
                response.end(Buffer.buffer(out.toByteArray()));
            }
        } catch (Exception e) {
            response.setStatusCode(404).end("No Found");
        }
    }
}
