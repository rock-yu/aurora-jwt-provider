package aurora.jwt.decoder.internal.payload

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * DTO representing JWT payload:
 *
 * <pre>
 * {
 * "version": 1,                // Payload version identifier to support backwards/forwards compatibility
 * "identity": {                // effectively the Subject of the request, in our domain form
 * "userId": "251540",        // represented as a strings for forwards compatibility
 * "organizationId": "9146"
 * },
 * "preferences": {
 * "locale": "en_au",
 * "timezone": "Australia/Melbourne",
 * "fileEncoding": "utf-8"
 * },
 * "authorization": {
 * "organization": "AE123",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "projects": {
 * "26905": "CJZ23",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "28318": "230A"         // ...
 * }
 * },
 * "exp": "1485415857"
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SecurityContextDto(
    var identity: IdentityDto? = null,
    var preferences: PreferencesDto? = null,
    var authorization: AuthorizationDto? = null
)

class PreferencesDto {
    var locale: String? = null
    var timezone: String? = null
    var fileEncoding: String? = null
}

// Json DTO
class AuthorizationDto {
    // this field is in Base36 bitmask encoded e.g: "11CTA32XOWQTUYMFBTIFDRD123VGD712UND8" which represents values of [2, 3, 6, 7, 8, 9, 10, 13, 14, 17, 23, 24, 28, 29, 30, 31, 46, 47, 48, 50, 54, 69, 70, 71, 74, 77, 78, 79, 80, 81, 82, 84, 86, 87, 95, 96, 103, 111, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 170, 181]
    var organization: String? = null
    var projects: Map<String, String>? = null
}

class IdentityDto {
    var userId: String? = null
    var organizationId: String? = null
}
