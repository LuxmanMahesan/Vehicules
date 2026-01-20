package com.ges.vehiclegate.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class ShiftInfo(
    val shiftNumber: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val agentName: String,
    val reportDate: LocalDate
) {
    val label: String
        get() = if (shiftNumber == 1) "Shift n°1 (07h-19h)" else "Shift n°2 (19h-07h)"
}

object ShiftCalculator {

    private val SHIFT_1_START = LocalTime.of(7, 0)
    private val SHIFT_2_START = LocalTime.of(19, 0)

    fun getCurrentShiftNumber(now: LocalDateTime = LocalDateTime.now()): Int {
        val time = now.toLocalTime()
        return if (time >= SHIFT_1_START && time < SHIFT_2_START) 1 else 2
    }

    fun getReportDateForShift(shiftNumber: Int, now: LocalDateTime = LocalDateTime.now()): LocalDate {
        return if (shiftNumber == 2) {
            val time = now.toLocalTime()
            if (time < SHIFT_1_START) {
                now.toLocalDate().minusDays(1)
            } else {
                now.toLocalDate()
            }
        } else {
            now.toLocalDate()
        }
    }

    fun getShiftStartTime(shiftNumber: Int, date: LocalDate): LocalDateTime {
        return if (shiftNumber == 1) {
            date.atTime(SHIFT_1_START)
        } else {
            date.atTime(SHIFT_2_START)
        }
    }

    fun getShiftEndTime(shiftNumber: Int, date: LocalDate): LocalDateTime {
        return if (shiftNumber == 1) {
            date.atTime(SHIFT_2_START)
        } else {
            date.plusDays(1).atTime(SHIFT_1_START)
        }
    }

    fun getNextShiftChangeTime(now: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        val time = now.toLocalTime()
        val date = now.toLocalDate()

        return when {
            time < SHIFT_1_START -> date.atTime(SHIFT_1_START)
            time < SHIFT_2_START -> date.atTime(SHIFT_2_START)
            else -> date.plusDays(1).atTime(SHIFT_1_START)
        }
    }

    fun getShiftStartMillis(shiftNumber: Int, date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return getShiftStartTime(shiftNumber, date).atZone(zoneId).toInstant().toEpochMilli()
    }

    fun getShiftEndMillis(shiftNumber: Int, date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return getShiftEndTime(shiftNumber, date).atZone(zoneId).toInstant().toEpochMilli()
    }
}
