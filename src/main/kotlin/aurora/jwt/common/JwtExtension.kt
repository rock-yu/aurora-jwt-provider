package aurora.jwt.decoder

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.text.ParseException
import java.time.Instant

fun JWTClaimsSet.signToken(signerKey: () -> String): SignedJWT = try {
    SignedJWT(JWSHeader(JWSAlgorithm.HS256), this).apply {
        this.sign(MACSigner(signerKey()))
    }
} catch (e: JOSEException) {
    throw SignTokenFailureException(e)
}

class SignTokenFailureException(cause: Throwable) :
    RuntimeException("Unexpected error occurred when signing token", cause)

internal fun SignedJWT.isVerifiedWith(
    secretKeysProvider: () -> List<String>,
    systemTime: Instant
) = secretKeysProvider().find { isVerifiedWith(it, systemTime) } != null

private fun SignedJWT.isVerifiedWith(secretKey: String, systemTime: Instant): Boolean = try {
    verify(MACVerifier(secretKey)) && notExpired(systemTime)
} catch (e: JOSEException) {
    throw JwtVerificationException("JWT signature verification failed", e)
} catch (e: ParseException) {
    throw JwtVerificationException("JWT signature verification failed", e)
}

@Throws(ParseException::class)
private fun SignedJWT.notExpired(systemTime: Instant): Boolean {
    val expiryDate = jwtClaimsSet.expirationTime.toInstant()
    if (!expiryDate.isAfter(systemTime)) {
        throw JwtVerificationException("JWT is expired")
    }
    return true
}
