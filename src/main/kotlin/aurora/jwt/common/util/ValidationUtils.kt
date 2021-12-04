package aurora.jwt.common.util

object ValidationUtils {
    fun checkArgument(expression: Boolean, errorMessage: String) {
        require(expression) { errorMessage }
    }

    fun notNullOrEmpty(str: String?, errorMessage: String): String {
        checkArgument(str != null && !str.isEmpty(), errorMessage)
        return str!!
    }
}
