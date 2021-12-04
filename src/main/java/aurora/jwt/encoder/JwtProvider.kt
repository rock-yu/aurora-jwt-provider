package aurora.jwt.encoder

import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.encoder.dto.AuthorizationJsonDtoFactory
import aurora.jwt.encoder.exception.SignTokenFailureException
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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
    private val signerKeyProvider: SignerKeyProvider,
    private val authorizationJsonDtoFactory: AuthorizationJsonDtoFactory = AuthorizationJsonDtoFactory(
        Base36BitmaskEncoder()
    )
) {
    fun generateJwtToken(tokenContextBuilder: TokenContextBuilder, expirationTimeInSeconds: Int): String {
        val securityContext = tokenContextBuilder.build()
        val claimsSet = JWTClaimsSet.Builder()
            .claim("version", JWT_CONTRACT_VERSION)
            .claim("identity", securityContext.identity.toJavaBean())
            .claim("preferences", securityContext.preferences)
            .claim("authorization", authorizationJsonDtoFactory.create(securityContext.authorization))
            .expirationTime(getExpirationAsDate(expirationTimeInSeconds))
            .build()
        return signToken(claimsSet).serialize()
    }

    fun generateJwtToken(securityContext: SecurityContext, expirationTimeInSeconds: Int): String {
        val claimsSet = JWTClaimsSet.Builder()
            .claim("version", JWT_CONTRACT_VERSION)
            .claim("identity", securityContext.identity.toJavaBean())
            .claim("preferences", securityContext.preferences)
            .claim("authorization", authorizationJsonDtoFactory.create(securityContext.authorization))
            .expirationTime(getExpirationAsDate(expirationTimeInSeconds))
            .build()
        return signToken(claimsSet).serialize()
    }

    private fun signToken(claimsSet: JWTClaimsSet): SignedJWT {
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        try {
            val signer: JWSSigner = MACSigner(signerKeyProvider.getKey())
            signedJWT.sign(signer)
        } catch (e: JOSEException) {
            throw SignTokenFailureException(e)
        }
        return signedJWT
    }

    private fun getExpirationAsDate(expirationInSeconds: Int): Date {
        val expiration = LocalDateTime.now().plus(expirationInSeconds.toLong(), ChronoUnit.SECONDS)
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())
    }

    companion object {
        private const val JWT_CONTRACT_VERSION = 1L
    }
}

// nimbusds serializer require the claim object to be in JavaBean convention (contain both setter/getter)
private fun Identity.toJavaBean() = IdentityBean(userId, organizationId)
private data class IdentityBean(var userId: String, var organizationId: String)
