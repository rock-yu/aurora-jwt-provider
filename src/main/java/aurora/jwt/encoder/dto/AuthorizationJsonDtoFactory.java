package aurora.jwt.encoder.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.common.dto.Authorization;

public class AuthorizationJsonDtoFactory {
    private Base36BitmaskEncoder bitmaskEncoder;

    public AuthorizationJsonDtoFactory(Base36BitmaskEncoder bitmaskEncoder) {
        this.bitmaskEncoder = bitmaskEncoder;
    }

    public AuthorizationJsonDto create(Authorization authorization) {
        if(authorization == null) {
            return null;
        }

        AuthorizationJsonDto jsonDto = new AuthorizationJsonDto();
        jsonDto.setOrganization(encodedOrganizationWiseAssets(authorization, bitmaskEncoder));
        jsonDto.setProjects(encodedProjectWiseAssets(authorization, bitmaskEncoder));
        return jsonDto;
    }

    private String encodedOrganizationWiseAssets(Authorization authorization, Base36BitmaskEncoder bitmaskEncoder) {
        return bitmaskEncoder.encode(authorization.organizationAssets());
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private Map<String, String> encodedProjectWiseAssets(Authorization authorization, Base36BitmaskEncoder bitmaskEncoder) {
        Map<String, List<Integer>> aProjectAssets = authorization.projectAssets();
        if (aProjectAssets == null || aProjectAssets.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> projects = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> projectAssets : aProjectAssets.entrySet()) {
            String projectId = projectAssets.getKey();
            String assetIdsEncoded = bitmaskEncoder.encode(projectAssets.getValue());
            projects.put(projectId, assetIdsEncoded);
        }

        return projects;
    }
}
