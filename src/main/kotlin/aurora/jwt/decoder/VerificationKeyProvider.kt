package aurora.jwt.decoder

fun interface VerificationKeyProvider {
    fun getKeys(): List<String>
}
