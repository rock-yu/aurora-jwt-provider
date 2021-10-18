package aurora.jwt.decoder;

import java.util.List;

public interface VerificationKeyProvider {
    List<String> getKeys();
}
