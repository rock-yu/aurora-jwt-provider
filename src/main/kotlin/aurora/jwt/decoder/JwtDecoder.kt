package aurora.jwt.decoder

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.internal.parseToSecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.SignedJWT
import java.io.IOException
import java.text.ParseException
import java.time.Instant

class JwtDecoder @JvmOverloads constructor(
    private val secretKeyProvider: SecretKeyProvider,
    private val objectMapper: ObjectMapper,
    private val decode: (String) -> List<Int> = { Base36BitmaskEncoder.decode(it) }
) {
    fun decodeJwt(jwt: String?, systemTime: Instant = Instant.now()): SecurityContext {
        return try {
            val signedJWT = SignedJWT.parse(jwt)
            if (!signedJWT.isVerifiedWith(secretKeyProvider::getKeys, systemTime)) {
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
