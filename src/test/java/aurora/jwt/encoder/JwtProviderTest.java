package aurora.jwt.encoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.encoder.dto.AuthorizationJsonDtoFactory;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JwtProviderTest {
    private static final String USER_ID = "1";
    private static final String ORGANIZATION_ID = "2";
    // 10010 (18)
    private List<Integer> organizationWiseAssets = Arrays.asList(71, 94);
    private Map<String, List<Integer>> projectWiseAssets = Collections.singletonMap("26905", Arrays.asList(11, 13));

    private JwtProvider testInstance;
    private static final int EXPIRATION_TIME_IN_SECONDS = 60;
    private static final String SECRET = "a658e59bc87c4bab8d585ddfaaa6894762d00de651614e8b987cf7a05389e23f";

    private TokenContextBuilder securityContext;

    private static class BitmaskEncoderStub extends Base36BitmaskEncoder {
        @Override
        public String encode(List<Integer> numbers) {
            return (numbers == null) ? "" : numbers.stream().map(number -> number.toString()).collect(Collectors.joining(","));
        }
    }


    @Mock
    private SignerKeyProvider sharedSecrets;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(sharedSecrets.getKey()).thenReturn(SECRET);
        this.testInstance = new JwtProvider(sharedSecrets, new AuthorizationJsonDtoFactory(new BitmaskEncoderStub()));

        this.securityContext =
                new TokenContextBuilder(USER_ID, ORGANIZATION_ID)
                        .withLocale("en_US")
                        .withTimezone("Australia/Sydney")
                        .withFileEncoding("utf-8")
                        .withOrganizationAssets(organizationWiseAssets)
                        .withProjectAssets(projectWiseAssets);
    }

    @Test
    public void createToken() throws JOSEException, ParseException {
        String jwtToken = this.testInstance.generateJwtToken(this.securityContext, EXPIRATION_TIME_IN_SECONDS);

        JWSVerifier verifier = new MACVerifier(SECRET);
        SignedJWT signedJWT = SignedJWT.parse(jwtToken);

        assertTrue(signedJWT.verify(verifier));

        JSONObject identity = (JSONObject) signedJWT.getJWTClaimsSet().getClaim("identity");
        assertEquals(USER_ID, identity.get("userId"));
        assertEquals(ORGANIZATION_ID, identity.get("organizationId"));

        JSONObject preferences = (JSONObject) signedJWT.getJWTClaimsSet().getClaim("preferences");
        assertEquals("en_US", preferences.get("locale"));
        assertEquals("Australia/Sydney", preferences.get("timezone"));

        JSONObject authorization = (JSONObject) signedJWT.getJWTClaimsSet().getClaim("authorization");
        assertEquals("71,94", authorization.get("organization"));
        assertEquals("11,13", ((JSONObject) authorization.get("projects")).get("26905"));
    }
}
