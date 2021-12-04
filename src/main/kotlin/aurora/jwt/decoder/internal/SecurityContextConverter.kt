package aurora.jwt.decoder.internal

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.Base36BitmaskEncoder
import aurora.jwt.decoder.internal.payload.AuthorizationDto
import aurora.jwt.decoder.internal.payload.IdentityDto
import aurora.jwt.decoder.internal.payload.PreferencesDto

class SecurityContextConverter(private val base36BitmaskEncoder: Base36BitmaskEncoder) {

    fun convert(
        identityDto: IdentityDto,
        preferencesDto: PreferencesDto,
        authorizationDto: AuthorizationDto
    ) = SecurityContext(
        identityDto.toIdentity(),
        preferencesDto.toPreferences(),
        authorizationDto.toAuthorization()
    )

    private fun IdentityDto.toIdentity() = Identity(
        requireNotNull(this.userId) { "'userId' can not be null" },
        requireNotNull(organizationId) { "'organizationId' can not be null" }
    )

    private fun PreferencesDto.toPreferences() = Preferences(this.locale, timezone, fileEncoding)

    private fun AuthorizationDto.toAuthorization(): Authorization {
        val decodedOrganizationWideAssets = base36BitmaskEncoder.decode(
            this.organization
        )

        // projectId -> Array[assets]
        val decodedProjectAssets = getProjectIdToSecuredAssetsMap(
            projects
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
