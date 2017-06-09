package com.dftc.logutils.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuqiqiang on 2017/6/2.
 */
public class ObjParser {

    public static final int INDENT = 4;
    // 基本数据类型
    private final static String[] TYPES = {"int", "java.lang.String", "boolean", "char",
            "float", "double", "long", "short", "byte"};
    private static String STR_INDENT;

    public static String parseObject(Object object) {
        return parseObject(object, 1);
    }

    public static String parseObject(Object object, int level) {
        if (object == null) {
            return "null";
        }

        if (object.getClass().isArray()) {
            return parseArray(object, level);
        } else if (object instanceof Collection) {
            return parseCollection(object, level);
        } else if (object instanceof Map) {
            return parseMap(object, level);
        } else {
            return objectToString(object, level);
        }
    }

    public static String parseArray(Object object, int level) {
        String simpleName = object.getClass().getSimpleName();
        StringBuilder msg;
        int dim = ArrayParser.getArrayDimension(object);
        switch (dim) {
            case 1:
                Pair pair = ArrayParser.arrayToString(object, level);
                msg = new StringBuilder(simpleName.replace("[]", "[" + pair.first + "] : "));
                msg.append(pair.second);
                break;
            case 2:
                Pair pair1 = ArrayParser.arrayToObject(object, level);
                Pair pair2 = (Pair) pair1.first;
                msg = new StringBuilder(simpleName.replace("[][]", "[" + pair2.first + "][" + pair2.second + "] : [\n"));
                msg.append(pair1.second).append(getIndent(level - 1)).append("]");
                break;
            default:
                msg = new StringBuilder(Arrays.deepToString((Object[]) object));
                // msg = new StringBuilder("{ Temporarily not support more than two dimensional Array! }");
                break;
        }
        return msg.toString();
    }

    @NonNull
    public static String parseCollection(Object object, int level) {
        String simpleName = object.getClass().getSimpleName();
        Collection collection = (Collection) object;
        StringBuilder builder = new StringBuilder(
                String.format(Locale.ENGLISH, "%s {size = %d} [", simpleName, collection.size())
        );
        if (!collection.isEmpty()) {
            builder.append("\n");
            String indent = getIndent(level);
            Iterator iterator = collection.iterator();
            int flag = 0;
            while (iterator.hasNext()) {
                Object item = iterator.next();
                builder.append(indent).append(String.format(Locale.ENGLISH, "[%d] : %s%s",
                        flag,
                        objectToString(item, level + 1),
                        flag++ < collection.size() - 1 ? ",\n" : "\n"));
            }
        }
        return builder.append(getIndent(level - 1)).append("]").toString();
    }

    @NonNull
    public static String parseMap(Object object, int level) {
        String simpleName = object.getClass().getSimpleName();
        Map map = (Map) object;
        Set keys = map.keySet();
        StringBuilder builder = new StringBuilder(
                String.format(Locale.ENGLISH, "%s {size = %d} [", simpleName, keys.size())
        );
        if (!keys.isEmpty()) {
            builder.append("\n");
            String indent = getIndent(level);
            int flag = 0;
            for (Object key : keys) {
                Object value = map.get(key);
                builder.append(indent).append(String.format(Locale.ENGLISH,
                        "[%s] -> %s%s", objectToString(key, level + 1), objectToString(value, level + 1),
                        flag++ < keys.size() - 1 ? ",\n" : "\n"));
            }
        }
        return builder.append(getIndent(level - 1)).append("]").toString();
    }

    public static <T> String objectToString(T object) {
        return objectToString(object, 1);
    }

    /**
     * 将对象转化为String
     */
    public static <T> String objectToString(T object, int level) {
        if (object == null) {
            return "Object{object is null}";
        }
        if (object.toString().startsWith(object.getClass().getName() + "@")) {
            StringBuilder builder = new StringBuilder(object.getClass().getSimpleName() + " {\n");
            Field[] fields = object.getClass().getDeclaredFields();
            String indent = getIndent(level);
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().startsWith("this$")) {
                    continue;
                }
                boolean flag = false;
                for (String type : TYPES) {
                    if (field.getType().getName().equalsIgnoreCase(type)) {
                        flag = true;
                        Object value = null;
                        try {
                            value = field.get(object);
                        } catch (IllegalAccessException e) {
                            value = e;
                        } finally {
                            builder.append(indent).append(String.format("%s = %s,\n", field.getName(),
                                    value == null ? "null" : value.toString()));
                        }
                    }
                }
                if (!flag) {
                    String value = null;
                    try {
                        value = parseObject(field.get(object), level + 1);
                    } catch (IllegalAccessException e) {
                        value = object.getClass().getSimpleName() + " { ??? }";
                    } finally {
                        builder.append(indent).append(String.format("%s = %s,\n", field.getName(), value));
                    }
                }
            }
            return builder.replace(builder.length() - 2, builder.length(), "\n")
                    .append(getIndent(level - 1))
                    .append("}").toString();
        } else {
            return object.toString();
        }
    }

    @NonNull
    public static String getIndent(int level) {
        if (TextUtils.isEmpty(STR_INDENT)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < INDENT; i++)
                builder.append(" ");
            STR_INDENT = builder.toString();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++)
            builder.append(STR_INDENT);
        return builder.toString();
    }

}
