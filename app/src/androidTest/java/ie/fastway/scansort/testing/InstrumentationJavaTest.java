package ie.fastway.scansort.testing;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests using Android Instrumentation using Java.
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentationJavaTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Assert.assertEquals("ie.fastway.scansort", appContext.getPackageName());
    }

}
