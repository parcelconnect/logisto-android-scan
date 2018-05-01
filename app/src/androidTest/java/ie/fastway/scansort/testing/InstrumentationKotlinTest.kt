package ie.fastway.scansort.testing

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that the InstrumentationRegistry works, and that Kotlin tests can be executed.
 */
@RunWith(AndroidJUnit4::class)
class InstrumentationKotlinTest {

    @Test
    fun testKotlinInstrument() {
        val appContext = InstrumentationRegistry.getTargetContext()
        Assert.assertEquals("ie.fastway.scansort", appContext.packageName)
    }


}