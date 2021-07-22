package cool.scx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
     * 类型工厂
     */
    private static final TypeFactory TYPE_FACTORY = XML_MAPPER.getTypeFactory();

    /**
     * map 类型
     */
    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {

    };

    static {
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        XML_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    }

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
    public static <T> T readValue(File xmlFile, Class<T> clazz) throws IOException {
        return XML_MAPPER.readValue(xmlFile, clazz);
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

    /**
     * <p>jsonNodeToBean.</p>
     *
     * @param jsonNode a {@link com.fasterxml.jackson.databind.JsonNode} object.
     * @param type     a {@link java.lang.Class} object.
     * @param <T>      a T object.
     * @return a T object.
     * @throws java.io.IOException if any.
     */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Type type) throws IOException {
        var reader = XML_MAPPER.readerFor(TYPE_FACTORY.constructType(type));
        return reader.readValue(jsonNode);
    }

}
