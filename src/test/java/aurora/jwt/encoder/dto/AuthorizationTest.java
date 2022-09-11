package aurora.jwt.encoder.dto;

import aurora.jwt.common.dto.Authorization;
import aurora.jwt.encoder.EncodedAuthorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static aurora.jwt.encoder.JwtProviderKt.encodedWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static String stubEncodeFunc(List<Integer> numbers) {
        return (numbers == null) ? "" : numbers.stream().map(number -> number.toString()).collect(Collectors.joining(","));
    }

    @BeforeEach
    public void setUp() {
        projectWiseAssets.put(project1Id, Arrays.asList(VIEW_ORG_CONFIDENTIAL_DOCUMENTS));
        projectWiseAssets.put(project2Id, Arrays.asList(VIEW_ORG_CONFIDENTIAL_DOCUMENTS, CAN_VIEW_ORG_TENDERS));

        this.authorization = new Authorization(orgWiseAssets, projectWiseAssets);
    }

    @Test
    public void testToJsonDtoWithBase36EncodedBitmask() {
        assertEquals("71", stubEncodeFunc(authorization.getOrganizationAssets()));

        EncodedAuthorization encodedAuthorization = encodedWith(authorization, numbers -> stubEncodeFunc(numbers));
        Map<String, String> projects = encodedAuthorization.getProjects();
        assertEquals(2, projects.size());
        assertEquals("94", projects.get(project1Id + ""));
        assertEquals("94,99", projects.get(project2Id + ""));
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
