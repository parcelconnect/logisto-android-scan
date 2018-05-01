package ie.fastway.scansort.testing;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import ie.fastway.scansort.scene.MainActivity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Tests that Robolectric has been configured correctly.
 */
@RunWith(RobolectricTestRunner.class)
public class RobolectricSmokeTest {

    @Test
    public void robolectric_canCreateMainActivity() throws Exception {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);

        assertThat(mainActivity, instanceOf(MainActivity.class));
    }
}