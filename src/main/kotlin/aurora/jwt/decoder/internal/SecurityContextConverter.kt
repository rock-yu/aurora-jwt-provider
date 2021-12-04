package aurora.jwt.decoder.internal

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.common.util.ValidationUtils
import aurora.jwt.decoder.internal.payload.AuthorizationDto
import aurora.jwt.decoder.internal.payload.IdentityDto
import aurora.jwt.decoder.internal.payload.PreferencesDto
import aurora.jwt.decoder.internal.payload.SecurityContextDto

class SecurityContextConverter(private val base36BitmaskEncoder: Base36BitmaskEncoder) {
    fun convert(payload: SecurityContextDto): SecurityContext {
        return SecurityContext(
            toIdentity(payload.identity),
            toPreferences(payload.preferences),
            toAuthorization(payload.authorization)
        )
    }

    private fun toIdentity(identityDto: IdentityDto?): Identity {
        val userId = ValidationUtils.notNullOrEmpty(identityDto!!.userId, "'userId' can not be null")
        val organizationId =
            ValidationUtils.notNullOrEmpty(identityDto.organizationId, "'organizationId' can not be null")
        return Identity(userId, organizationId)
    }

    private fun toPreferences(preferencesDto: PreferencesDto?): Preferences {
        return Preferences(preferencesDto!!.locale, preferencesDto.timezone, preferencesDto.fileEncoding)
    }

    private fun toAuthorization(authorizationDto: AuthorizationDto?): Authorization {
        val decodedOrganizationWideAssets = base36BitmaskEncoder.decode(
            authorizationDto!!.organization
        )

        // projectId -> Array[assets]
        val decodedProjectAssets = getProjectIdToSecuredAssetsMap(
            authorizationDto.projects
        )
        return Authorization(decodedOrganizationWideAssets, decodedProjectAssets)
    }

    private fun getProjectIdToSecuredAssetsMap(projects: Map<String, String>?): Map<String, List<Int>> {
        if (projects == null || projects.isEmpty()) {
            return emptyMap()
        }
        val decodedProjectAssets: MutableMap<String, List<Int>> = LinkedHashMap()
        for ((projectId, value) in projects) {
            val decodedSecuredAssets = base36BitmaskEncoder.decode(value)
            decodedProjectAssets[projectId] = decodedSecuredAssets
        }
        return decodedProjectAssets
    }
}
