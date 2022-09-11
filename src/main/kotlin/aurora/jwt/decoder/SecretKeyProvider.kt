package aurora.jwt.decoder

fun interface SecretKeyProvider {
    fun getKeys(): List<String>
}
