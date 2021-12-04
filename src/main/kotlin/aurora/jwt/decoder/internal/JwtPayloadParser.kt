package aurora.jwt.decoder.internal

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.decoder.internal.payload.PreferencesDto
import aurora.jwt.decoder.internal.payload.SecurityContextDto
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

class JwtPayloadParser(
    private val objectMapper: ObjectMapper,
    private val securityContextConverter: SecurityContextConverter
) {
    @Throws(IOException::class)
    fun parse(jwtPayload: ByteArray): SecurityContext {
        val payload: SecurityContextDto = objectMapper.readValue(jwtPayload, SecurityContextDto::class.java)

        return payload.toSecurityContext()
    }

    private fun SecurityContextDto.toSecurityContext(): SecurityContext {
        val identity = requireNotNull(identity) { "'identity' is not provided" }
        require(!identity.userId.isNullOrEmpty()) {
            "'identity.userId' is not provided"
        }

        require(
            !identity.organizationId.isNullOrEmpty()
        ) {
            "'identity.organizationId' is not provided"
        }

        return securityContextConverter.convert(
            identity,
            preferences ?: PreferencesDto(),
            requireNotNull(authorization) { "'authorization' is not provided" }
        )
    }
}
