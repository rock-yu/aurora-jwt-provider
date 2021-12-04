package aurora.jwt.decoder.internal

import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.ValidationUtils
import aurora.jwt.decoder.internal.payload.SecurityContextDto
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

class JwtPayloadParser(
    private val objectMapper: ObjectMapper,
    private val securityContextConverter: SecurityContextConverter
) {
    @Throws(IOException::class)
    fun parse(jwtPayload: ByteArray?): SecurityContext {
        val payload = objectMapper.readValue(jwtPayload, SecurityContextDto::class.java)
        validatePayloadJson(payload)
        return securityContextConverter.convert(payload)
    }

    private fun validatePayloadJson(payload: SecurityContextDto) {
        val identity = requireNotNull(payload.identity) { "'identity' is not provided" }
        ValidationUtils.checkArgument(
            identity.userId != null && identity.userId!!.isNotEmpty(),
            "'identity.userId' is not provided"
        )
        ValidationUtils.checkArgument(
            identity.organizationId != null && identity.organizationId!!.isNotEmpty(),
            "'identity.organizationId' is not provided"
        )
    }
}
