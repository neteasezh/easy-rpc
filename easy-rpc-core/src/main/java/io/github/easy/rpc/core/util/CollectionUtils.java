package io.github.easy.rpc.core.util;

import java.util.Collection;

/**
 * @author zhuhai
 * @date 2023/12/25
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean contains(Collection collection, Object obj) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(obj);

    }

}
