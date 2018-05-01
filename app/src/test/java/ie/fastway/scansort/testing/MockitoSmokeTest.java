package ie.fastway.scansort.testing;


import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ie.fastway.scansort.R;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


/**
 * Tests that Mockito and the Mockito test runner have been set up correcly.
 */
@RunWith(MockitoJUnitRunner.class)
public class MockitoSmokeTest {

    private static final String FAKE_STRING = "GOOD MORNING WORLD";

    @Mock
    Context mockContext;

    @Test
    public void readStringFromContext_LocalisedString() {
        // Given a mocked Context injected into the object under test...
        when(mockContext.getString(R.string.hello_world))
                .thenReturn(FAKE_STRING);

        // ...when the string is returned by the Context...
        String result = mockContext.getString(R.string.hello_world);

        // ...then the result is the mocked string resource.
        assertThat(result, is(FAKE_STRING));
    }

}
