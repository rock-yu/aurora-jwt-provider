package aurora.jwt.decoder.internal

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.internal.payload.AuthorizationDto
import aurora.jwt.decoder.internal.payload.IdentityDto
import aurora.jwt.decoder.internal.payload.PreferencesDto
import aurora.jwt.decoder.internal.payload.SecurityContextDto
import com.fasterxml.jackson.databind.ObjectMapper

internal fun ByteArray.parseToSecurityContext(
    objectMapper: ObjectMapper,
    decode: (String) -> List<Int> = { Base36BitmaskEncoder.decode(it) }
): SecurityContext =
    objectMapper.readValue(this, SecurityContextDto::class.java).let {
        return SecurityContext(
            it.identity.toIdentity(),
            (it.preferences ?: PreferencesDto()).toPreferences(),
            requireNotNull(it.authorization) { "'authorization' is not provided" }
                .toAuthorization(decode)
        )
    }

private fun IdentityDto?.toIdentity(): Identity =
    requireNotNull(this) { "'identity' is not provided" }
        .let {
            Identity(
                requireNotNull(userId) { "'identity.userId' is not provided" },
                requireNotNull(organizationId) { "'identity.organizationId' is not provided" }
            )
        }

private fun AuthorizationDto.toAuthorization(decode: (String) -> List<Int>) = Authorization(
    decode(organization.orEmpty()),
    projects.orEmpty().map { (projectId, assetsEncoded) -> projectId to decode(assetsEncoded) }.toMap()
)

private fun PreferencesDto.toPreferences() = Preferences(this.locale, timezone, fileEncoding)
