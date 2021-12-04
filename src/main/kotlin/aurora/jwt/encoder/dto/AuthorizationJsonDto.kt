package aurora.jwt.encoder.dto

/**
 * DTO representing authorization structure:
 *
 * "authorization": {
 * "organization": "AE123",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "projects": {
 * "26905": "CJZ23",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "28318": "230A"         // ...
 * }
 * }
 */
class AuthorizationJsonDto {
    var organization: String? = null
    var projects: Map<String, String>? = null
}
