package aurora.jwt.decoder;

import static aurora.jwt.common.util.ValidationUtils.checkArgument;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import aurora.jwt.common.dto.SecurityContext;
import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.decoder.exception.JwtVerificationException;
import aurora.jwt.decoder.internal.JwtPayloadParser;
import aurora.jwt.decoder.internal.SecurityContextConverter;

public class JwtDecoder {
    private final JwtPayloadParser jwtPayloadParser;
    private final aurora.jwt.decoder.VerificationKeyProvider verificationKeyProvider;

    public JwtDecoder(aurora.jwt.decoder.VerificationKeyProvider verificationKeyProvider, ObjectMapper objectMapper) {
        this(verificationKeyProvider, objectMapper,  new SecurityContextConverter(new Base36BitmaskEncoder()));
    }

    protected JwtDecoder(aurora.jwt.decoder.VerificationKeyProvider verificationKeyProvider, ObjectMapper objectMapper, SecurityContextConverter securityContextConverter) {
        checkArgument(verificationKeyProvider != null, "'verificationKeyProvider' can not be null");
        this.verificationKeyProvider = verificationKeyProvider;
        this.jwtPayloadParser = new JwtPayloadParser(objectMapper, securityContextConverter);
    }

    public SecurityContext decodeJwt(String jwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);

            if (!verify(signedJWT)) {
                throw new JwtVerificationException("JWT signature verification failed");
            }

            return jwtPayloadParser.parse(signedJWT.getPayload().toBytes());
        } catch (IOException | ParseException e) {
            throw new JwtVerificationException("JWT parsing exception", e);
        }
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private boolean verify(SignedJWT signedJWT) {
        List<String> secretKeys = verificationKeyProvider.getKeys();
        for (String secretKey : secretKeys) {
            if (verify(signedJWT, secretKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean verify(SignedJWT signedJWT, String secretKey) {
        try {
            JWSVerifier verifier = new MACVerifier(secretKey);
            return signedJWT.verify(verifier) && verifyDate(signedJWT);
        } catch (JOSEException | ParseException e) {
            throw new JwtVerificationException("JWT signature verification failed", e);
        }
    }

    private boolean verifyDate(SignedJWT signedJWT) throws ParseException {
        Instant expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

        if (!expiryDate.isAfter(Instant.now())) {
            throw new JwtVerificationException("JWT is expired");
        }
        return true;
    }
}
