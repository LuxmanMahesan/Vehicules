package com.ges.vehiclegate.util

import java.time.LocalDate
import java.time.ZoneId

interface DateTimeProvider {
    fun nowMillis(): Long
    fun startOfTodayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long
    fun startOfTomorrowMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long
}

class SystemDateTimeProvider : DateTimeProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()

    override fun startOfTodayMillis(zoneId: ZoneId): Long =
        LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()

    override fun startOfTomorrowMillis(zoneId: ZoneId): Long =
        LocalDate.now(zoneId).plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
}
