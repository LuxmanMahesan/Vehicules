package com.ges.vehiclegate.domain.model

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

object AgentManager {

    private const val PREFS_NAME = "agent_prefs"
    private const val KEY_AGENT_NAME = "agent_name"
    private const val KEY_SHIFT_NUMBER = "shift_number"
    private const val KEY_SHIFT_START_MILLIS = "shift_start_millis"

    const val SITE_NAME = "JARDIN D'ACCLIMATATION"

    private var prefs: SharedPreferences? = null

    private val _agentName = MutableStateFlow("")
    val agentName: StateFlow<String> = _agentName.asStateFlow()

    private val _currentShift = MutableStateFlow(1)
    val currentShift: StateFlow<Int> = _currentShift.asStateFlow()

    private val _shiftStartMillis = MutableStateFlow(0L)
    val shiftStartMillis: StateFlow<Long> = _shiftStartMillis.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadFromPrefs()
        }
    }

    private fun loadFromPrefs() {
        prefs?.let { p ->
            _agentName.value = p.getString(KEY_AGENT_NAME, "") ?: ""
            _currentShift.value = p.getInt(KEY_SHIFT_NUMBER, ShiftCalculator.getCurrentShiftNumber())
            _shiftStartMillis.value = p.getLong(KEY_SHIFT_START_MILLIS, 0L)
        }
    }

    fun setAgent(name: String, shiftNumber: Int, shiftStartMillis: Long) {
        _agentName.value = name
        _currentShift.value = shiftNumber
        _shiftStartMillis.value = shiftStartMillis

        prefs?.edit()?.apply {
            putString(KEY_AGENT_NAME, name)
            putInt(KEY_SHIFT_NUMBER, shiftNumber)
            putLong(KEY_SHIFT_START_MILLIS, shiftStartMillis)
            apply()
        }
    }

    fun needsShiftChange(): Boolean {
        val currentComputedShift = ShiftCalculator.getCurrentShiftNumber()
        val savedShift = _currentShift.value
        val agentEmpty = _agentName.value.isBlank()

        return agentEmpty || currentComputedShift != savedShift
    }

    fun getAgentDisplayName(): String {
        return _agentName.value.ifBlank { "Agent non défini" }
    }

    fun getCurrentShiftInfo(): ShiftInfo {
        val now = LocalDateTime.now()
        val shiftNumber = ShiftCalculator.getCurrentShiftNumber(now)
        val reportDate = ShiftCalculator.getReportDateForShift(shiftNumber, now)

        return ShiftInfo(
            shiftNumber = shiftNumber,
            startTime = ShiftCalculator.getShiftStartTime(shiftNumber, reportDate),
            endTime = ShiftCalculator.getShiftEndTime(shiftNumber, reportDate),
            agentName = getAgentDisplayName(),
            reportDate = reportDate
        )
    }
}
