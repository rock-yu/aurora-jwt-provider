package aurora.jwt.common.dto

data class SecurityContext(
    val identity: Identity,
    val preferences: Preferences,
    val authorization: Authorization
)
