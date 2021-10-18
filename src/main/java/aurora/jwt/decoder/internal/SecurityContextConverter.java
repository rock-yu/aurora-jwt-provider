package aurora.jwt.decoder.internal;

import static aurora.jwt.common.util.ValidationUtils.notNullOrEmpty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aurora.jwt.common.dto.Authorization;
import aurora.jwt.common.dto.Identity;
import aurora.jwt.common.dto.Preferences;
import aurora.jwt.common.dto.SecurityContext;
import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.decoder.internal.payload.AuthorizationDto;
import aurora.jwt.decoder.internal.payload.IdentityDto;
import aurora.jwt.decoder.internal.payload.PreferencesDto;
import aurora.jwt.decoder.internal.payload.SecurityContextDto;


public class SecurityContextConverter {
    private final Base36BitmaskEncoder base36BitmaskEncoder;

    public SecurityContextConverter(Base36BitmaskEncoder base36BitmaskEncoder) {
        this.base36BitmaskEncoder = base36BitmaskEncoder;
    }

    public SecurityContext convert(SecurityContextDto payload) {
        return new SecurityContext(
                toIdentity(payload.getIdentity()),
                toPreferences(payload.getPreferences()),
                toAuthorization(payload.getAuthorization()));
    }

    private Identity toIdentity(IdentityDto identityDto) {
        String userId = notNullOrEmpty(identityDto.getUserId(), "'userId' can not be null");
        String organizationId = notNullOrEmpty(identityDto.getOrganizationId(), "'organizationId' can not be null");
        return new Identity(userId, organizationId);
    }

    private Preferences toPreferences(PreferencesDto preferencesDto) {
        return new Preferences(preferencesDto.getLocale(), preferencesDto.getTimezone(), preferencesDto.getFileEncoding());
    }

    private Authorization toAuthorization(AuthorizationDto authorizationDto) {
        List<Integer> decodedOrganizationWideAssets =
                this.base36BitmaskEncoder.decode(authorizationDto.getOrganization());

        // projectId -> Array[assets]
        Map<String, List<Integer>> decodedProjectAssets =
                getProjectIdToSecuredAssetsMap(authorizationDto.getProjects());

        return new Authorization(decodedOrganizationWideAssets, decodedProjectAssets);
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private Map<String, List<Integer>> getProjectIdToSecuredAssetsMap(Map<String, String> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<Integer>> decodedProjectAssets = new LinkedHashMap<>();
        for (Map.Entry<String, String> projectIdToEncodedAssets : projects.entrySet()) {
            String projectId = projectIdToEncodedAssets.getKey();
            List<Integer> decodedSecuredAssets =
                    this.base36BitmaskEncoder.decode(projectIdToEncodedAssets.getValue());

            decodedProjectAssets.put(projectId, decodedSecuredAssets);
        }
        return decodedProjectAssets;
    }
}
