package aurora.jwt.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <E> List<E> nullSafeList(List<E> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <K, V> Map<K, V> nullSafeMap(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }
}
