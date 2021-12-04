package aurora.jwt.decoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.jwt.common.dto.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;
import aurora.jwt.common.dto.Authorization;
import aurora.jwt.common.dto.Preferences;
import aurora.jwt.common.dto.SecurityContext;
import aurora.jwt.decoder.exception.JwtVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtDecoderTest {
    private static final String KEY = "secret73243274473237248328932484328234";
    // private static int EXPIRATION = 60 * 60 * 24 * 365; //1 year

    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        this.jwtDecoder = new JwtDecoder(() -> Arrays.asList(KEY), new ObjectMapper());
    }

    @Test
    void shouldVerifyValidJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpemF0aW9uIjp7InByb2plY3RzIjp7IjUwMyI6IjRVWk1HSlNYWThUS0ZKS1pLIiwiMTg3OTA0ODQwMCI6IjRVWk1HSlNYWThUS0ZKS1pLIiwiMTg3OTA0ODQwMSI6IjRVWk1HSlNYWThUS0ZKS1pLIn0sIm9yZ2FuaXphdGlvbiI6IjJENUxCNjBWMlRUUVJXSVFMRDdEODVURk8xUUY0In0sInByZWZlcmVuY2VzIjp7ImZpbGVFbmNvZGluZyI6InV0Zi04IiwidGltZXpvbmUiOiJBdXN0cmFsaWFcL01lbGJvdXJuZSIsImxvY2FsZSI6ImVuX2F1In0sImlkZW50aXR5Ijp7Im9yZ2FuaXphdGlvbklkIjoiMTg3OTA0ODQ5MiIsInVzZXJJZCI6IjIyIn0sImV4cCI6MTY1MjQ4NzMzOCwidmVyc2lvbiI6MX0.bpcGlTL4vi1-EXCgW4sKY22RYAHPX9GPq_5C3yoJApo";
        SecurityContext securityContext = jwtDecoder.decodeJwt(jwt);

        ensureIdentityIsPresent(securityContext);
        ensureAuthorizationIsPresent(securityContext);
        ensurePreferencesIsPresent(securityContext);
    }

    @Test
    void shouldRaiseExceptionForInvalidJwt() {
        String invalidJwtFormat = "invalid jwt token";
        try {
            jwtDecoder.decodeJwt(invalidJwtFormat);
        } catch (JwtVerificationException e) {
            assertEquals("JWT parsing exception", e.getMessage());
        }
    }

    @Test
    void shouldRaiseExceptionForValidButNotVerifiedJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpemF0aW9uIjp7Im9yZ2FuaXphdGlvbiI6IjJENUxCNjBWMlRUUVJXSVFMRDdEODVURk8xUUY0IiwicHJvamVjdHMiOnsiNTAzIjoiNFVaTUdKU1hZOFRLRkpLWksiLCIxODc5MDQ4NDAwIjoiNFVaTUdKU1hZOFRLRkpLWksiLCIxODc5MDQ4NDAxIjoiNFVaTUdKU1hZOFRLRkpLWksifX0sImlkZW50aXR5Ijp7InVzZXJJZCI6IjIyIiwib3JnYW5pemF0aW9uSWQiOiIxODc5MDQ4NDkyIn0sInByZWZlcmVuY2VzIjp7ImxvY2FsZSI6ImVuX2F1IiwidGltZXpvbmUiOiJBdXN0cmFsaWEvTWVsYm91cm5lIiwiZmlsZUVuY29kaW5nIjoidXRmLTgifSwiZXhwIjoxNjEzNjA1NTUxLCJ2ZXJzaW9uIjoxfQ.G0YPM5b4_YeGyUrLe5aj52qN89Vx3tgSa-gCLW1xX2Q";
        try {
            jwtDecoder.decodeJwt(jwt);
        } catch (JwtVerificationException e) {
            assertEquals("JWT signature verification failed", e.getMessage());
        }
    }

    private void ensureIdentityIsPresent(SecurityContext securityContext) {
        Identity identity = securityContext.getIdentity();
        assertEquals("1879048492", identity.getOrganizationId());
        assertEquals("22", identity.getUserId());
    }

    private void ensurePreferencesIsPresent(SecurityContext securityContext) {
        Preferences preferences = securityContext.getPreferences();
        assertEquals("en_au", preferences.getLocale());
        assertEquals("Australia/Melbourne", preferences.getTimezone());
        assertEquals("utf-8", preferences.getFileEncoding());
    }

    private void ensureAuthorizationIsPresent(SecurityContext securityContext) {
        Authorization authorization = securityContext.getAuthorization();

        assertEquals(Arrays.asList(146), authorization.organizationAssets());

        Map<String, List<Integer>> expectedProjectAssets = new HashMap<>();
        expectedProjectAssets.put("503", Arrays.asList(85));
        expectedProjectAssets.put("1879048400", Arrays.asList(85));
        expectedProjectAssets.put("1879048401", Arrays.asList(85));
        assertEquals(expectedProjectAssets, authorization.projectAssets());
    }
}
