package com.jm.utils;


public class ClassUtils {
    public ClassUtils() {
    }

    public static <T> T cast(Object obj, Class<T> clazz) {
        return obj != null && !clazz.isInstance(obj) ? null : (T) obj;
    }


}
