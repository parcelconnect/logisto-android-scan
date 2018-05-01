package ie.fastway.scansort.logging

import android.content.Context
import android.util.Log
import com.logentries.logger.AndroidLogger
import ie.fastway.scansort.config.SecretProvider
import timber.log.Timber

/**
 * A [Timber.Tree] that writes log messages to Logentries.
 */
class LogentriesTree(applicationContext: Context) : Timber.Tree() {

    private val logentries = AndroidLogger.createInstance(
            applicationContext, false, true, false, null, 0,
            SecretProvider.LOGENTRIES_ACCESS_TOKEN, true
    )

    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {

        // NOTE: We don't wrap this in `LogConfig.LOGENTRIES`, since that should only be used
        // to determine whether or not this tree is planted.

        try {
            logentries.log("priority=$priority; tag=$tag; message=<$message>; throwable=$t;")
        }
        catch (e: Throwable) {
            // We can't log this error using Timber, since logging it will call this method again,
            // which will cause a StackOverflow.
            // Instead, we use the raw Android Log.
            Log.e("LogentriesTree", "Cannot write to Logentries. errorMessage=${e.message};")
        }
    }
}
