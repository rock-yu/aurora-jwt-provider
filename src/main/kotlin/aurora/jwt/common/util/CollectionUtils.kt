package aurora.jwt.common.util

object CollectionUtils {
    fun <E> nullSafeList(list: List<E>?): List<E> {
        return list ?: emptyList()
    }

    fun <K, V> nullSafeMap(map: Map<K, V>?): Map<K, V> {
        return map ?: emptyMap()
    }
}
