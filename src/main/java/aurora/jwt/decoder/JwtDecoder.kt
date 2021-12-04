package aurora.jwt.decoder

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.exception.JwtVerificationException
import aurora.jwt.decoder.internal.JwtPayloadParser
import aurora.jwt.decoder.internal.SecurityContextConverter
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import java.io.IOException
import java.text.ParseException
import java.time.Instant

class JwtDecoder protected constructor(
    verificationKeyProvider: VerificationKeyProvider,
    objectMapper: ObjectMapper,
    securityContextConverter: SecurityContextConverter
) {
    private val jwtPayloadParser: JwtPayloadParser
    private val verificationKeyProvider: VerificationKeyProvider

    constructor(verificationKeyProvider: VerificationKeyProvider, objectMapper: ObjectMapper) : this(
        verificationKeyProvider,
        objectMapper,
        SecurityContextConverter(Base36BitmaskEncoder())
    )

    fun decodeJwt(jwt: String?): SecurityContext {
        return try {
            val signedJWT = SignedJWT.parse(jwt)
            if (!verify(signedJWT)) {
                throw JwtVerificationException("JWT signature verification failed")
            }
            jwtPayloadParser.parse(signedJWT.payload.toBytes())
        } catch (e: IOException) {
            throw JwtVerificationException("JWT parsing exception", e)
        } catch (e: ParseException) {
            throw JwtVerificationException("JWT parsing exception", e)
        }
    }

    private fun verify(signedJWT: SignedJWT): Boolean {
        val secretKeys = verificationKeyProvider.getKeys()
        for (secretKey in secretKeys) {
            if (verify(signedJWT, secretKey)) {
                return true
            }
        }
        return false
    }

    private fun verify(signedJWT: SignedJWT, secretKey: String): Boolean {
        return try {
            val verifier: JWSVerifier = MACVerifier(secretKey)
            signedJWT.verify(verifier) && verifyDate(signedJWT)
        } catch (e: JOSEException) {
            throw JwtVerificationException("JWT signature verification failed", e)
        } catch (e: ParseException) {
            throw JwtVerificationException("JWT signature verification failed", e)
        }
    }

    @Throws(ParseException::class)
    private fun verifyDate(signedJWT: SignedJWT): Boolean {
        val expiryDate = signedJWT.jwtClaimsSet.expirationTime.toInstant()
        if (!expiryDate.isAfter(Instant.now())) {
            throw JwtVerificationException("JWT is expired")
        }
        return true
    }

    init {
        this.verificationKeyProvider = verificationKeyProvider
        jwtPayloadParser = JwtPayloadParser(objectMapper, securityContextConverter)
    }
}
