package cool.scx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 针对 jackson xmlMapper 进行一些简单的封装
 *
 * @author scx567888
 * @version 1.3.0
 */
public class XmlUtils {

    /**
     * xml 解析器
     */
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    /**
     * map 类型
     */
    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {

    };

    /**
     * 读取 xml 为 map
     *
     * @param xmlFile Xml 文件
     * @return map
     * @throws java.io.IOException io异常
     */
    public static Map<String, Object> readToMap(File xmlFile) throws IOException {
        return XML_MAPPER.readValue(xmlFile, mapType);
    }

    /**
     * 读取 xml 为 map
     *
     * @param xmlFile Xml 文件
     * @return map
     * @throws java.io.IOException io异常
     */
    public static JsonNode readTree(File xmlFile) throws IOException {
        return XML_MAPPER.readTree(xmlFile);
    }

}
