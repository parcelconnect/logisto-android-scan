package ie.logistio.equinox;

import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class EquinoxTest {

    private static final String TIME_2017_02_03_111000 = "2017-02-03 11:10:00";

    private final Instant timeToConvertSummer = Instant.parse("2017-06-01T12:13:14Z");

    @Before
    public void setUp() throws Exception {
        Equinox.reset();
    }

    @Test
    public void convertInstantToTimestamp_duringDst() throws Exception {
        Instant duringDst = Instant.parse("2017-08-30T14:00:00Z");

        String convertedTimestamp = Equinox.convertInstantToTimestamp(duringDst);

        assertEquals("2017-08-30 14:00:00", convertedTimestamp);
    }

    @Test
    public void convertInstantToTimestamp_outsideDst() throws Exception {
        Instant outsideDst = Instant.parse("2017-02-15T18:00:00Z");

        String convertedTimestamp = Equinox.convertInstantToTimestamp(outsideDst);

        assertEquals("2017-02-15 18:00:00", convertedTimestamp);
    }

    @Test
    public void test_convertInstantToDate() throws Exception {
        Instant dateToConvert = Instant.parse("2017-02-03T11:12:13Z");

        String convertedDate = Equinox.convertInstantToDate(dateToConvert);
        assertEquals("2017-02-03", convertedDate);
    }

    @Test
    public void testConvertToSqlFormat() throws Exception {
        String convertedValue = Equinox.convertToSqlFormat(null, "DEFAULT");
        assertEquals("DEFAULT", convertedValue);

        Instant timestamp = Instant.parse("2017-02-15T18:00:00Z");
        convertedValue = Equinox.convertToSqlFormat(timestamp, "FAIL");
        assertEquals("2017-02-15 18:00:00", convertedValue);
    }

    @Test
    public void testIsApproxEqual() throws Exception {
        Instant instant1 = toInstant(TIME_2017_02_03_111000);
        Instant instant2 = instant1.plus(400L, ChronoUnit.MILLIS);

        assertTrue(Equinox.isApproximatelyEqual(instant1, instant2));
        // Order of arguments shouldn't matter:
        assertTrue(Equinox.isApproximatelyEqual(instant2, instant1));

        Instant instantLater = instant1.plus(1L, ChronoUnit.SECONDS);
        assertFalse(Equinox.isApproximatelyEqual(instant1, instantLater));
        // Flip the arguments
        assertFalse(Equinox.isApproximatelyEqual(instantLater, instant1));
    }

    private Instant toInstant(String timestamp) {
        return Equinox.convertTimestampToInstant(timestamp);
    }
}