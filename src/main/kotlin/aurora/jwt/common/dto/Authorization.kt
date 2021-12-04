package aurora.jwt.common.dto

import java.util.Collections

class Authorization(
    private val organizationAssets: List<Int>,
    private val projectAssets: Map<String, List<Int>>
) {
    fun hasOrganizationWideAsset(assetId: Int): Boolean {
        return organizationAssets.contains(assetId)
    }

    fun isGrantedAssetForProject(projectId: String, assetId: Int): Boolean {
        return if (notAuthorizedForProject(projectId)) {
            false
        } else projectAssets[projectId]!!.contains(assetId)
    }

    fun notAuthorizedForProject(projectId: String): Boolean = projectAssets.containsKey(projectId).not()

    fun organizationAssets(): List<Int> {
        return Collections.unmodifiableList(organizationAssets)
    }

    fun projectAssets(): Map<String, List<Int>> {
        return Collections.unmodifiableMap(projectAssets)
    }
}
