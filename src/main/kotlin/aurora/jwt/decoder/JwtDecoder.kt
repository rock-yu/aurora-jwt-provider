package aurora.jwt.decoder

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.internal.parseToSecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.SignedJWT
import java.io.IOException
import java.text.ParseException

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

