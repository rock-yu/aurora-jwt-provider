package aurora.jwt.common.dto;

public class Identity {
    private String userId;
    private String organizationId;

    public Identity() {}
    public Identity(String userId, String organizationId) {
        this.userId = userId;
        this.organizationId = organizationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
