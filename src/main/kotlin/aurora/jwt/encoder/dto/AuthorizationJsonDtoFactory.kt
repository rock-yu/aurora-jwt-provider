package aurora.jwt.encoder.dto

import aurora.jwt.common.dto.Authorization
import aurora.jwt.common.util.Base36BitmaskEncoder

class AuthorizationJsonDtoFactory(private val bitmaskEncoder: Base36BitmaskEncoder) {

    fun create(authorization: Authorization) = AuthorizationJsonDto(
        organization = bitmaskEncoder.encode(authorization.organizationAssets),
        projects = authorization.projectAssets.map { (projectId, assets) -> projectId to bitmaskEncoder.encode(assets) }.toMap()
    )
}
