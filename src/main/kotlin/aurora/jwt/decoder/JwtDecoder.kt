package aurora.jwt.decoder

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.internal.parseToSecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import java.io.IOException
import java.text.ParseException
import java.time.Instant

class JwtDecoder @JvmOverloads constructor(
    private val verificationKeys: VerificationKeyProvider,
    private val objectMapper: ObjectMapper,
    private val decode: (String) -> List<Int> = { Base36BitmaskEncoder.decode(it) }
) {
    fun decodeJwt(jwt: String?): SecurityContext {
        return try {
            val signedJWT = SignedJWT.parse(jwt)
            if (!signedJWT.isVerifiedWith(verificationKeys::getKeys)) {
                throw JwtVerificationException("JWT signature verification failed")
            }

            signedJWT.payload.toBytes().parseToSecurityContext(objectMapper, decode)
        } catch (e: IOException) {
            throw JwtVerificationException("JWT parsing exception", e)
        } catch (e: ParseException) {
            throw JwtVerificationException("JWT parsing exception", e)
        }
    }
}

class JwtVerificationException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}

internal fun SignedJWT.isVerifiedWith(verificationKeys: () -> List<String>) =
    verificationKeys().find { isVerifiedWith(it) } != null

private fun SignedJWT.isVerifiedWith(secretKey: String): Boolean = try {
    verify(MACVerifier(secretKey)) && notExpired()
} catch (e: JOSEException) {
    throw JwtVerificationException("JWT signature verification failed", e)
} catch (e: ParseException) {
    throw JwtVerificationException("JWT signature verification failed", e)
}

@Throws(ParseException::class)
private fun SignedJWT.notExpired(): Boolean {
    val expiryDate = jwtClaimsSet.expirationTime.toInstant()
    if (!expiryDate.isAfter(Instant.now())) {
        throw JwtVerificationException("JWT is expired")
    }
    return true
}
