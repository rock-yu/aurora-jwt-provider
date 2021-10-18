package aurora.jwt.encoder.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aurora.jwt.common.util.Base36BitmaskEncoder;
import aurora.jwt.common.dto.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationTest {

    // granted
    public static final int VIEW_GLOBAL_DIRECTORY = 71;

    public static final int VIEW_ORG_CONFIDENTIAL_DOCUMENTS = 94;
    public static final int CAN_VIEW_ORG_TENDERS = 99;

    // denied
    private static final int CAN_CONFIGURE_USER_NOTIFICATION_TYPE = 81;

    private List<Integer> orgWiseAssets = Arrays.asList(VIEW_GLOBAL_DIRECTORY);
    private Map<String, List<Integer>> projectWiseAssets = new LinkedHashMap<>();
    private String project1Id = "26905";
    private String project2Id = "23354";

    private Authorization authorization;

    private static class BitmaskEncoderStub extends Base36BitmaskEncoder {
        @Override
        public String encode(List<Integer> numbers) {
            return (numbers == null) ? "" : numbers.stream().map(number -> number.toString()).collect(Collectors.joining(","));
        }
    }

    @BeforeEach
    public void setUp() {
        projectWiseAssets.put(project1Id, Arrays.asList(VIEW_ORG_CONFIDENTIAL_DOCUMENTS));
        projectWiseAssets.put(project2Id, Arrays.asList(VIEW_ORG_CONFIDENTIAL_DOCUMENTS, CAN_VIEW_ORG_TENDERS));

        this.authorization = new Authorization(orgWiseAssets, projectWiseAssets);
    }

    @Test
    public void testToJsonDtoWithBase36EncodedBitmask() {
        AuthorizationJsonDto jsonDto = new AuthorizationJsonDtoFactory(new BitmaskEncoderStub()).create(authorization);
        assertEquals("71", jsonDto.getOrganization());

        Map<String, String> projects = jsonDto.getProjects();
        assertEquals(2, projects.size());
        assertEquals("94", projects.get(project1Id + ""));
        assertEquals("94,99", projects.get(project2Id + ""));
    }

    @Test
    public void testToJsonDtoWithEmptyAssets() {
        Authorization emptyAuthorization = new Authorization(null, null);

        AuthorizationJsonDto jsonDto = new AuthorizationJsonDtoFactory(new BitmaskEncoderStub()).create(emptyAuthorization);
        assertEquals("", jsonDto.getOrganization());

        Map<String, String> projects = jsonDto.getProjects();
        assertEquals(0, projects.size());
    }

    @Test
    public void hasOrganizationWideAsset() {
        assertTrue(this.authorization.hasOrganizationWideAsset(VIEW_GLOBAL_DIRECTORY));
        assertFalse(this.authorization.hasOrganizationWideAsset(CAN_CONFIGURE_USER_NOTIFICATION_TYPE));

        assertTrue(this.authorization.isGrantedAssetForProject(project1Id, VIEW_ORG_CONFIDENTIAL_DOCUMENTS));
        assertTrue(this.authorization.isGrantedAssetForProject(project2Id, VIEW_ORG_CONFIDENTIAL_DOCUMENTS));
        assertTrue(this.authorization.isGrantedAssetForProject(project2Id, CAN_VIEW_ORG_TENDERS));

        assertFalse(this.authorization.isGrantedAssetForProject(project1Id, CAN_VIEW_ORG_TENDERS));
    }
}
