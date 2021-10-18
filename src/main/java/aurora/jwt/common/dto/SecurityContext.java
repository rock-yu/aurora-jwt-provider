package aurora.jwt.common.dto;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class SecurityContext {

    private Identity identity;
    private Preferences preferences;
    private Authorization authorization;

    public SecurityContext(Identity identity, Preferences preferences, Authorization authorization) {
        this.identity = identity;
        this.preferences = preferences;
        this.authorization = authorization;
    }

    public Identity getIdentity() {
        return identity;
    }
    public Preferences getPreferences() {
        return preferences;
    }
    public Authorization getAuthorization() {
        return authorization;
    }
}
