package aurora.jwt.encoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import aurora.jwt.common.dto.SecurityContext;
import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.encoder.dto.AuthorizationJsonDtoFactory;
import aurora.jwt.encoder.exception.SignTokenFailureException;

/**
 * Builder to create JWT token with the following contract:
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
 *     "organization": "0x23AB",   // Base-36 encoded form of the number that represents the Bitmask (see below)
 *     "projects": {
 *       "26905": "0x34CD",        // Base-36 encoded form of the number that represents the Bitmask (see below)
 *       "28318": "0xAB23"         // ...
 *     }
 *   },
 *   "exp": "1485415857"
 * }
 */
public class JwtProvider {
    private final aurora.jwt.encoder.SignerKeyProvider signerKeyProvider;
    private final AuthorizationJsonDtoFactory authorizationJsonDtoFactory;
    private static final Long JWT_CONTRACT_VERSION = 1L;

    public JwtProvider(aurora.jwt.encoder.SignerKeyProvider signerKeyProvider) {
        this(signerKeyProvider, new AuthorizationJsonDtoFactory(new Base36BitmaskEncoder()));
    }

    public JwtProvider(aurora.jwt.encoder.SignerKeyProvider signerKeyProvider, AuthorizationJsonDtoFactory authorizationJsonDtoFactory) {
        this.signerKeyProvider = signerKeyProvider;
        this.authorizationJsonDtoFactory = authorizationJsonDtoFactory;
    }

    public String generateJwtToken(aurora.jwt.encoder.TokenContextBuilder tokenContextBuilder, int expirationTimeInSeconds) {
        SecurityContext securityContext = tokenContextBuilder.build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("version", JWT_CONTRACT_VERSION)
                .claim("identity", securityContext.getIdentity())
                .claim("preferences", securityContext.getPreferences())
                .claim("authorization", authorizationJsonDtoFactory.create(securityContext.getAuthorization()))
                .expirationTime(getExpirationAsDate(expirationTimeInSeconds))
                .build();
        return signToken(claimsSet).serialize();
    }

    public String generateJwtToken(SecurityContext securityContext, int expirationTimeInSeconds) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("version", JWT_CONTRACT_VERSION)
                .claim("identity", securityContext.getIdentity())
                .claim("preferences", securityContext.getPreferences())
                .claim("authorization", authorizationJsonDtoFactory.create(securityContext.getAuthorization()))
                .expirationTime(getExpirationAsDate(expirationTimeInSeconds))
                .build();
        return signToken(claimsSet).serialize();
    }

    private SignedJWT signToken(JWTClaimsSet claimsSet) {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            JWSSigner signer = new MACSigner(signerKeyProvider.getKey());
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new SignTokenFailureException(e);
        }
        return signedJWT;
    }

    private Date getExpirationAsDate(int expirationInSeconds) {
        LocalDateTime expiration = LocalDateTime.now().plus(expirationInSeconds, ChronoUnit.SECONDS);
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }
}
