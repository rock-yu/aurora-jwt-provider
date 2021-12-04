package aurora.jwt.decoder.internal.payload

// Json DTO
class AuthorizationDto {
    // decode "11CTA32XOWQTUYMFBTIFDRD123VGD712UND8" to:
    // [2, 3, 6, 7, 8, 9, 10, 13, 14, 17, 23, 24, 28, 29, 30, 31, 46, 47, 48, 50, 54, 69, 70, 71, 74, 77, 78, 79, 80, 81, 82, 84, 86, 87, 95, 96, 103, 111, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 170, 181]
    var organization: String? = null
    var projects: Map<String, String>? = null
}
