package aurora.jwt.common.dto

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
