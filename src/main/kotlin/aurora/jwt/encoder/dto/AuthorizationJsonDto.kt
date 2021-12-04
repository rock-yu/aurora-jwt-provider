package aurora.jwt.encoder.dto

/**
 * DTO representing authorization structure:
 *
 * <pre>
 * "authorization": {
 *   "organization": "AE123",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 *   "projects": {
 *     "26905": "CJZ23",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 *     "28318": "230A"         // ...
 *   }
 * }
 * </pre>
 */
data class AuthorizationJsonDto(
    val organization: String,
    val projects: Map<String, String>
)
