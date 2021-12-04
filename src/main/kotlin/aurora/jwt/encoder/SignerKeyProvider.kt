package aurora.jwt.encoder

fun interface SignerKeyProvider {
    fun getKey(): String
}
