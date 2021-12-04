package aurora.jwt.encoder.dto

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.util.Base36BitmaskEncoder

class AuthorizationJsonDtoFactory(private val bitmaskEncoder: Base36BitmaskEncoder) {
    fun create(authorization: Authorization?): AuthorizationJsonDto? {
        if (authorization == null) {
            return null
        }
        val jsonDto = AuthorizationJsonDto()
        jsonDto.organization = encodedOrganizationWiseAssets(authorization, bitmaskEncoder)
        jsonDto.projects = encodedProjectWiseAssets(authorization, bitmaskEncoder)
        return jsonDto
    }

    private fun encodedOrganizationWiseAssets(
        authorization: Authorization,
        bitmaskEncoder: Base36BitmaskEncoder
    ): String {
        return bitmaskEncoder.encode(authorization.organizationAssets())
    }

    private fun encodedProjectWiseAssets(
        authorization: Authorization,
        bitmaskEncoder: Base36BitmaskEncoder
    ): Map<String, String> {
        val aProjectAssets = authorization.projectAssets()
        if (aProjectAssets.isEmpty()) {
            return emptyMap()
        }
        val projects: MutableMap<String, String> = LinkedHashMap()
        for ((projectId, value) in aProjectAssets) {
            val assetIdsEncoded = bitmaskEncoder.encode(value)
            projects[projectId] = assetIdsEncoded
        }
        return projects
    }
}
