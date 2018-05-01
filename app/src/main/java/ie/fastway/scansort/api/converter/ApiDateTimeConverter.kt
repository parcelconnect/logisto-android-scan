package ie.fastway.scansort.api.converter

import ie.logistio.equinox.Equinox
import ie.logistio.paloma.adapters.InstantJsonAdapter
import org.threeten.bp.Instant


/**
 *
 */
class ApiDateTimeConverter : InstantJsonAdapter.InstantConverter {

    override fun convertFromInstantToTimestamp(instant: Instant): String {
        return Equinox.convertInstantToTimestamp(instant)
    }

    override fun convertFromTimestampToInstant(timestamp: String): Instant {
        return Equinox.convertTimestampToInstant(timestamp)
    }
}