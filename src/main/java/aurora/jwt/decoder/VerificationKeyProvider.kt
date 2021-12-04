package aurora.jwt.decoder

interface VerificationKeyProvider {
    fun getKeys(): List<String>
}
