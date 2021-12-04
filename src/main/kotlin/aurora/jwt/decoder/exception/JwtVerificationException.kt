package aurora.jwt.decoder.exception

import java.lang.RuntimeException

class JwtVerificationException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(message: String?) : super(message) {}
}
