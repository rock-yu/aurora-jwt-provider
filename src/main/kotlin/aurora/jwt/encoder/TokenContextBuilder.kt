package aurora.jwt.encoder

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext

class TokenContextBuilder(
    private val userId: String,
    private val organizationId: String
) {
    private var locale: String? = null
    private var timezone: String? = null
    private var fileEncoding: String? = null

    private var organizationAssets: List<Int> = emptyList()
    private var projectAssets: Map<String, List<Int>> = emptyMap()

    fun withLocale(locale: String) = apply { this.locale = locale }
    fun withTimezone(timezone: String) = apply { this.timezone = timezone }
    fun withFileEncoding(fileEncoding: String) = apply { this.fileEncoding = fileEncoding }
    fun withOrganizationAssets(organizationAssets: List<Int>) = apply { this.organizationAssets = organizationAssets }
    fun withProjectAssets(projectAssets: Map<String, List<Int>>) = apply { this.projectAssets = projectAssets }

    fun build() = SecurityContext(
        Identity(userId, organizationId),
        Preferences(locale, timezone, fileEncoding),
        Authorization(
            organizationAssets,
            projectAssets
        )
    )
}
