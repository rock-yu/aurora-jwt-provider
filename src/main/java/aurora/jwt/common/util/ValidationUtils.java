package aurora.jwt.common.util;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static String notNullOrEmpty(String str, String errorMessage) {
        checkArgument(str != null && !str.isEmpty(), errorMessage);
        return str;
    }
}
