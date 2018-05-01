package ie.fastway.scansort.views.networkinfo

import ie.logistio.equinox.Equinox
import org.threeten.bp.Instant

data class UserWaitingEventUpdate(
        val taskId: String,
        val userMessage: String,
        val progressValue: Int? = null,
        val progressMax: Int? = null,
        val eventAt: Instant = Equinox.now(),
        val isPending: Boolean = true
) {

    object Factory {
        fun onEventFinished(taskId: String): UserWaitingEventUpdate {
            return UserWaitingEventUpdate(
                    taskId = taskId,
                    userMessage = "Finished",
                    isPending = false
            )
        }
    }
}