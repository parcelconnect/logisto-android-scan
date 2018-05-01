package ie.fastway.scansort

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.MaterialModule
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.logging.LogentriesTree
import timber.log.Timber

/**
 * Application class for the Fastway ScanSort app.
 *
 * This is registered in `AndroidManifest.xml`.
 */
class FastwayScanSortApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LogConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (LogConfig.LOGENTRIES) {
            val logentriesTree = LogentriesTree(applicationContext)
            Timber.plant(logentriesTree)
        }

        Timber.d("FastwayScanSortApp; onCreate()")

        // Initialise the JSR-310 timezones:
        AndroidThreeTen.init(this)

        // Initialise Icon sets:
        Iconify.with(MaterialModule())
    }

}
