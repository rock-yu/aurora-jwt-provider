package aurora.jwt.encoder

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.common.util.secondsLater
import aurora.jwt.common.util.toDate
import aurora.jwt.decoder.signToken
import com.nimbusds.jwt.JWTClaimsSet
import java.time.LocalDateTime
import java.util.Date
/**
 * Builder to create JWT token with the following contract:
 *
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
 * "organization": "0x23AB",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "projects": {
 * "26905": "0x34CD",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 * "28318": "0xAB23"         // ...
 * }
 * },
 * "exp": "1485415857"
 * }
 */
class JwtProvider @JvmOverloads constructor(
    private val signerKey: SignerKeyProvider,
    private val assetsEncoder: (List<Int>) -> String = { Base36BitmaskEncoder.encode(it) }
) {

    fun generateJwt(
        jwtTokenContextBuilder: JwtTokenContextBuilder,
        expirationTime: Date,
    ): String {
        val securityContext = jwtTokenContextBuilder.build()
        val claimsSet = JWTClaimsSet.Builder()
            .claim("version", JWT_CONTRACT_VERSION)
            .claim("identity", securityContext.identity.toJavaBean())
            .claim("preferences", securityContext.preferences.toJavaBean())
            .claim("authorization", securityContext.authorization.encodedWith(assetsEncoder).toJavaBean())
            .expirationTime(expirationTime)
            .build()
        return claimsSet.signToken(signerKey::getKey).serialize()
    }

    fun generateJwt(
        tokenContextBuilder: JwtTokenContextBuilder,
        expirationTimeInSeconds: Int
    ): String = generateJwt(tokenContextBuilder, LocalDateTime.now().secondsLater(expirationTimeInSeconds).toDate())

    companion object {
        private const val JWT_CONTRACT_VERSION = 1L
    }
}

// nimbusds serializer require the claim object to be in JavaBean convention (contain both setter/getter)
private fun Identity.toJavaBean() = IdentityBean(userId, organizationId)
private fun Preferences.toJavaBean() = PreferencesBean(locale, timezone, fileEncoding)
private fun EncodedAuthorization.toJavaBean() = AuthorizationJsonBean(organization, projects)

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
data class EncodedAuthorization(val organization: String, val projects: Map<String, String>)
data class IdentityBean(var userId: String, var organizationId: String)
data class PreferencesBean(var locale: String?, var timezone: String?, var fileEncoding: String?)
data class AuthorizationJsonBean(var organization: String, var projects: Map<String, String>)

fun Authorization.encodedWith(encoder: (List<Int>) -> String) =
    EncodedAuthorization(
        encoder(organizationAssets),
        encodeProjectAssets(projectAssets, encoder)
    )

private fun encodeProjectAssets(
    projectAssets: Map<String, List<Int>>,
    encoder: (List<Int>) -> String
) = projectAssets.map { (projectId, assets) -> projectId to encoder(assets) }.toMap()
