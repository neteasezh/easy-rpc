package com.netease.easy.rpc.core.util;

import java.util.Arrays;

/**
 * @author zhuhai
 * @date 2023/12/22
 */
public class StringUtils {
    public static final String EMPTY = "";
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (css == null || css.length == 0) {
            return true;
        }
        return Arrays.stream(css).anyMatch(s -> s == null || s.length() == 0);
    }

    public static boolean isNoneEmpty(CharSequence... css) {
        return !isAnyEmpty(css);
    }

    public static boolean isAllEmpty(CharSequence... css) {
        if (css == null || css.length == 0) {
            return true;
        }
        return Arrays.stream(css).allMatch(s -> s == null || s.length() == 0);
    }

}
