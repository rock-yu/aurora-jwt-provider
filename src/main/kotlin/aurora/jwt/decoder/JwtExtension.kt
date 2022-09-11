package aurora.jwt.decoder

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import java.text.ParseException
import java.time.Instant

internal fun SignedJWT.isVerifiedWith(secretKeysProvider: () -> List<String>) =
    secretKeysProvider().find { isVerifiedWith(it) } != null

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
