package aurora.jwt.encoder.dto;

import java.util.Map;

/**
 * DTO representing authorization structure:
 *
 * "authorization": {
 *     "organization": "AE123",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 *     "projects": {
 *       "26905": "CJZ23",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 *       "28318": "230A"         // ...
 *     }
 * }
 */
public class AuthorizationJsonDto {
    private String organization;
    private Map<String, String> projects;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Map<String, String> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, String> projects) {
        this.projects = projects;
    }
}
