package aurora.jwt.common.dto

class Preferences {
    var locale: String? = null
    var timezone: String? = null
    var fileEncoding: String? = null

    constructor() {}
    constructor(locale: String?, timezone: String?, fileEncoding: String?) {
        this.locale = locale
        this.timezone = timezone
        this.fileEncoding = fileEncoding
    }
}
