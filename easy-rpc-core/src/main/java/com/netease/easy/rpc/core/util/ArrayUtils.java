package com.netease.easy.rpc.core.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author zhuhai
 * @date 2023/12/25
 */
public class ArrayUtils {
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean contains(Object[] array, Object obj) {
        if (isEmpty(array)) {
            return false;
        }
        return Arrays.stream(array).collect(Collectors.toList()).contains(obj);

    }
}
