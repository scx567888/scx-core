package cool.scx.sql;

import java.util.HashMap;

public class SQLHelper {
    public static String getSQlColumnTypeByClass(Class<?> clazz) {
        var TypeMapping = new HashMap<Class<?>, String>();
        TypeMapping.put(java.lang.Integer.class, "int");
        TypeMapping.put(java.lang.Long.class, "bigint");
        TypeMapping.put(java.lang.Double.class, "double");
        TypeMapping.put(java.lang.Boolean.class, "BIT(1)");
        TypeMapping.put(java.time.LocalDateTime.class, "DATETIME");
        var type = TypeMapping.get(clazz);
        if (type == null) {
            return "varchar(128)";
        }
        return type;
    }

}
