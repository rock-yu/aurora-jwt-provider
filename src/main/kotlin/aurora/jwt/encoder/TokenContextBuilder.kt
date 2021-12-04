package aurora.jwt.encoder

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext

class TokenContextBuilder(userId: String?, organizationId: String?) {
    private val userId: String
    private val organizationId: String
    private var locale: String? = null
    private var timezone: String? = null
    private var fileEncoding: String? = null
    private var organizationAssets: List<Int> = emptyList()
    private var projectAssets: Map<String, List<Int>> = emptyMap()
    fun withLocale(locale: String?): TokenContextBuilder {
        this.locale = locale
        return this
    }

    fun withTimezone(timezone: String?): TokenContextBuilder {
        this.timezone = timezone
        return this
    }

    fun withFileEncoding(fileEncoding: String?): TokenContextBuilder {
        this.fileEncoding = fileEncoding
        return this
    }

    fun withOrganizationAssets(organizationAssets: List<Int>): TokenContextBuilder {
        this.organizationAssets = organizationAssets
        return this
    }

    fun withProjectAssets(projectAssets: Map<String, List<Int>>): TokenContextBuilder {
        this.projectAssets = projectAssets
        return this
    }

    fun build(): SecurityContext {
        return SecurityContext(
            Identity(userId, organizationId),
            Preferences(locale, timezone, fileEncoding),
            Authorization(
                organizationAssets,
                projectAssets
            )
        )
    }

    init {
        this.userId = requireNotNull(userId) { "'userId' is required" }
        this.organizationId = requireNotNull(organizationId) { "'organizationId' is required" }
    }
}
