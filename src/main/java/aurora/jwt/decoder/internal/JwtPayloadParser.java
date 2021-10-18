package aurora.jwt.decoder.internal;

import static aurora.jwt.common.util.ValidationUtils.checkArgument;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import aurora.jwt.common.dto.SecurityContext;
import aurora.jwt.decoder.internal.payload.IdentityDto;
import aurora.jwt.decoder.internal.payload.SecurityContextDto;

public class JwtPayloadParser {
    private final ObjectMapper objectMapper;
    private final aurora.jwt.decoder.internal.SecurityContextConverter securityContextConverter;

    public JwtPayloadParser(ObjectMapper objectMapper, aurora.jwt.decoder.internal.SecurityContextConverter securityContextConverter) {
        this.objectMapper = objectMapper;
        this.securityContextConverter = securityContextConverter;
    }

    public SecurityContext parse(byte[] jwtPayload) throws IOException {
        SecurityContextDto payload =
                objectMapper.readValue(jwtPayload, SecurityContextDto.class);
        validatePayloadJson(payload);
        return securityContextConverter.convert(payload);
    }

    private void validatePayloadJson(SecurityContextDto payload) {
        IdentityDto identity = payload.getIdentity();
        checkArgument(identity != null, "'identity' is not provided");
        checkArgument(identity.getUserId() != null && !identity.getUserId().isEmpty(), "'identity.userId' is not provided");
        checkArgument(identity.getOrganizationId() != null && !identity.getOrganizationId().isEmpty(), "'identity.organizationId' is not provided");
    }


}
