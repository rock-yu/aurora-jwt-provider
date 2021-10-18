package aurora.jwt.encoder;

import static aurora.jwt.common.util.CollectionUtils.nullSafeList;
import static aurora.jwt.common.util.CollectionUtils.nullSafeMap;
import static aurora.jwt.common.util.ValidationUtils.notNullOrEmpty;

import java.util.List;
import java.util.Map;

import aurora.jwt.common.dto.Authorization;
import aurora.jwt.common.dto.Identity;
import aurora.jwt.common.dto.Preferences;
import aurora.jwt.common.dto.SecurityContext;

public class TokenContextBuilder {
    private String userId;
    private String organizationId;

    private String locale;
    private String timezone;
    private String fileEncoding;
    private List<Integer> organizationAssets;
    private Map<String, List<Integer>> projectAssets;

    public TokenContextBuilder(String userId, String organizationId) {
        this.userId = notNullOrEmpty(userId, "'userId' is required");
        this.organizationId = notNullOrEmpty(organizationId, "'organizationId' is required");
    }

    public TokenContextBuilder withLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public TokenContextBuilder withTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public TokenContextBuilder withFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
        return this;
    }

    public TokenContextBuilder withOrganizationAssets(List<Integer> organizationAssets) {
        this.organizationAssets = organizationAssets;
        return this;
    }

    public TokenContextBuilder withProjectAssets(Map<String, List<Integer>> projectAssets) {
        this.projectAssets = projectAssets;
        return this;
    }

    public SecurityContext build() {
        return new SecurityContext(
                new Identity(this.userId, this.organizationId),
                new Preferences(this.locale, this.timezone, this.fileEncoding),
                new Authorization(
                        nullSafeList(this.organizationAssets),
                        nullSafeMap(this.projectAssets)));
    }
}
