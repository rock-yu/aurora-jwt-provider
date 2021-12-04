package aurora.jwt.encoder

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.dto.Identity
import aurora.jwt.common.dto.Preferences
import aurora.jwt.common.dto.SecurityContext
import aurora.jwt.common.util.CollectionUtils
import aurora.jwt.common.util.ValidationUtils

class TokenContextBuilder(userId: String?, organizationId: String?) {
    private val userId: String
    private val organizationId: String
    private var locale: String? = null
    private var timezone: String? = null
    private var fileEncoding: String? = null
    private var organizationAssets: List<Int>? = null
    private var projectAssets: Map<String, List<Int>>? = null
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

    fun withOrganizationAssets(organizationAssets: List<Int>?): TokenContextBuilder {
        this.organizationAssets = organizationAssets
        return this
    }

    fun withProjectAssets(projectAssets: Map<String, List<Int>>?): TokenContextBuilder {
        this.projectAssets = projectAssets
        return this
    }

    fun build(): SecurityContext {
        return SecurityContext(
            Identity(userId, organizationId),
            Preferences(locale, timezone, fileEncoding),
            Authorization(
                CollectionUtils.nullSafeList(organizationAssets),
                CollectionUtils.nullSafeMap(projectAssets)
            )
        )
    }

    init {
        this.userId = ValidationUtils.notNullOrEmpty(userId, "'userId' is required")
        this.organizationId = ValidationUtils.notNullOrEmpty(organizationId, "'organizationId' is required")
    }
}
