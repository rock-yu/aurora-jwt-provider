package aurora.jwt.decoder.internal.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO representing JWT payload:
 *
 * {
 *   "version": 1,                // Payload version identifier to support backwards/forwards compatibility
 *   "identity": {                // effectively the Subject of the request, in our domain form
 *     "userId": "251540",        // represented as a strings for forwards compatibility
 *     "organizationId": "9146"
 *   },
 *   "preferences": {
 *     "locale": "en_au",
 *     "timezone": "Australia/Melbourne",
 *     "fileEncoding": "utf-8"
 *   },
 *   "authorization": {
 *     "organization": "AE123",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 *     "projects": {
 *       "26905": "CJZ23",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 *       "28318": "230A"         // ...
 *     }
 *   },
 *   "exp": "1485415857"
 * }
 */
@SuppressWarnings("PMD.ExcessiveParameterList")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityContextDto {

    private IdentityDto identity;
    private PreferencesDto preferences;
    private AuthorizationDto authorization;

    public IdentityDto getIdentity() {
        return identity;
    }

    public void setIdentity(IdentityDto identity) {
        this.identity = identity;
    }

    public PreferencesDto getPreferences() {
        return preferences;
    }

    public void setPreferences(PreferencesDto preferences) {
        this.preferences = preferences;
    }

    public AuthorizationDto getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationDto authorization) {
        this.authorization = authorization;
    }
}
