package aurora.jwt.common.dto

class SecurityContext(
    val identity: Identity,
    val preferences: Preferences,
    val authorization: Authorization
)
