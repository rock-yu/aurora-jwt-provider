package aurora.jwt.common.dto

data class SecurityContext(
    val identity: Identity,
    val preferences: Preferences,
    val authorization: Authorization
)

data class Identity(val userId: String, val organizationId: String)

data class Preferences(val locale: String?, val timezone: String?, val fileEncoding: String?)

class Authorization(
    val organizationAssets: List<Int>,
    val projectAssets: Map<String, List<Int>>
) {
    fun hasOrganizationWideAsset(assetId: Int) = organizationAssets.contains(assetId)

    fun isGrantedAssetForProject(projectId: String, assetId: Int): Boolean {
        return if (notAuthorizedForProject(projectId)) {
            false
        } else projectAssets[projectId]!!.contains(assetId)
    }

    fun notAuthorizedForProject(projectId: String): Boolean = projectAssets.containsKey(projectId).not()
}
