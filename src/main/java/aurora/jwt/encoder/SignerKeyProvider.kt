package aurora.jwt.encoder

interface SignerKeyProvider {
    fun getKey(): String
}
