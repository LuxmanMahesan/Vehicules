package com.ges.vehiclegate.ui.navigation

object Routes {
    const val HOME = "home"
    const val ADD = "add_vehicle"
    const val TODAY = "today"

    const val EDIT = "edit_vehicle"
    const val EDIT_WITH_ARG = "edit_vehicle/{id}"
    const val HISTORY = "history"
    const val EMAIL_CONFIG = "email_config"

    fun editRoute(id: Long) = "$EDIT/$id"
}
