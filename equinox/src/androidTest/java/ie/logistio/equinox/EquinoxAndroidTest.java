package ie.logistio.equinox;

import android.support.test.InstrumentationRegistry;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EquinoxAndroidTest {

    private final Instant timeToConvertWinter = Instant.parse("2017-01-01T12:13:14Z");
    private final Instant timeToConvertSummer = Instant.parse("2017-06-01T12:13:14Z");

    @Before
    public void setUp() throws Exception {
        Equinox.reset();
        AndroidThreeTen.init(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void convertToLocalTimeOfDay() throws Exception {
        ZoneId irelandTimezone = ZoneId.of("Europe/Dublin");
        Equinox.setUserTimeZone(irelandTimezone);

        // When DST is active...
        String convertedTime = Equinox.convertToLocalTimeOfDay(timeToConvertSummer);
        assertEquals("13:13:14", convertedTime);

        // When DST is not active...
        convertedTime = Equinox.convertToLocalTimeOfDay(timeToConvertWinter);
        assertEquals("12:13:14", convertedTime);

    }

}