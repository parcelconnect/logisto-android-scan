package com.tubbert.powdroid

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 *
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentationKotlinTest {

    /* --------------------------------------------------------------------------------------------
    | NOTE:
    | Sometimes Android Studio produces a "No tests found" error when running tests that are
    | written in Kotlin.
    | To work around this issue, run the package that the Kotlin test is in, rather than
    | the Kotlin test class itself; in this case AndroidStudio will find all of the
    | test methods.
    |
    */

    @Test
    fun testKotlinInstrument() {
        val appContext = InstrumentationRegistry.getTargetContext()
        Assert.assertEquals("com.tubbert.powdroid.test", appContext.packageName)
    }


}