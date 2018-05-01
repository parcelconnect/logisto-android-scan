package ie.fastway.scansort.device.printer

import org.threeten.bp.Instant

/**
 * A message to print to a label.
 */
data class LabelMessage(
        val labelType: Int,
        val primaryText: String?,
        val secondaryText: String? = null,
        val timestamp: Instant?= null
)

