package aurora.jwt.common.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

fun LocalDateTime.secondsLater(numberOfSeconds: Int) =
    plus(numberOfSeconds.toLong(), ChronoUnit.SECONDS)!!

fun LocalDateTime.toDate() = Date.from(atZone(ZoneId.systemDefault()).toInstant())!!
