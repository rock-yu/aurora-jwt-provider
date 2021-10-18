package aurora.jwt.common.dto;

import static aurora.jwt.common.util.CollectionUtils.nullSafeList;
import static aurora.jwt.common.util.CollectionUtils.nullSafeMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Authorization {
    private List<Integer> organizationAssets;
    private Map<String, List<Integer>> projectAssets;

    public Authorization(
            List<Integer> organizationAssets,
            Map<String, List<Integer>> projectAssets) {
        this.organizationAssets = nullSafeList(organizationAssets);
        this.projectAssets = nullSafeMap(projectAssets);
    }

    public boolean hasOrganizationWideAsset(int assetId) {
        return organizationAssets != null && organizationAssets.contains(assetId);
    }

    public boolean isGrantedAssetForProject(String projectId, int assetId) {
        if (this.projectAssets == null || notAuthorizedForProject(projectId)) {
            return false;
        }

        return this.projectAssets.get(projectId).contains(assetId);
    }

    public boolean notAuthorizedForProject(String projectId) {
        return !this.projectAssets.containsKey(projectId);
    }

    public List<Integer> organizationAssets() {
        return Collections.unmodifiableList(this.organizationAssets);
    }

    public Map<String, List<Integer>> projectAssets() {
        return Collections.unmodifiableMap(this.projectAssets);
    }
}
