package aurora.jwt.encoder.exception

import java.lang.RuntimeException

class SignTokenFailureException(cause: Throwable?) :
    RuntimeException("Unexpected error occurred when signing token", cause)
