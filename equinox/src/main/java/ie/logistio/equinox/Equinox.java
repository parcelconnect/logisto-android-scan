package ie.logistio.equinox;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalAccessor;

import java.util.Locale;


/**
 * Time util library.
 * <p>
 * All timestamps sent via APIs should communicate using UTC.
 * DateTimes should be formatted as "yyyy-MM-dd HH:mm:ss".
 * Dates should be formatted as "yyyy-MM-dd".
 * <p>
 * Local times are never used for APIs.
 */
public class Equinox {

    private static final ZoneId ZONE_UTC = ZoneId.of("Z");

    private static final DateTimeFormatter FORMATTER_DATE;

    private static final DateTimeFormatter FORMATTER_DATETIME;

    private static final long DEFAULT_APPROX_EQUAL_THRESHOLD_MS = 1000L;

    @NonNull
    private static ZoneId userTimezone;

    static {

        FORMATTER_DATE = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withLocale(Locale.UK)
                .withZone(ZoneId.of("Z"));

        FORMATTER_DATETIME = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HH:mm:ss")
                .withLocale(Locale.UK)
                .withZone(ZoneId.of("Z"));

        reset();

        /*
        FORMATTER_DATETIME = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter(Locale.UK);
        */
    }

    public static void reset() {
        userTimezone = ZONE_UTC;
    }

    public static Instant epoch() {
        return Instant.EPOCH;
    }


    @NonNull
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Gets the current time, accurate to the second.
     * @return
     */
    public static Instant nowSecond() {
        return now().truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Gets the current time-of-day in a format that the user expects.
     *
     * @return A human-readable time, presented in a format the user expects.
     * e.g. "HH:mm:ss".
     */
    public static String nowAsUserTime() {
        return convertToLocalTimeOfDay(now());
    }

    /**
     * Gets the current time as a unix timestamp (seconds since the Epoch)
     *
     * @return The number of seconds that have passed since 1970-01-01 00:00:00 UTC.
     */
    public static long nowUnixTimestamp() {
        return Instant.now().getEpochSecond();
    }

    public static Instant createFromUnixTimestamp(long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }

    /**
     * Gets the current time in the format used by MySQL (as UTC)
     *
     * @return The current UTC time in format "yyyy-MM-dd HH:mm:ss"
     */
    public static String nowMysqlDatetime() {
        return convertInstantToTimestamp(now());
    }

    /**
     * Converts to calandar date.
     *
     * @param instantToConvert
     *         The time to convert to a calendar date.
     *
     * @return The calandar date that the given instant falls on
     * in UTC timezone. Format: "yyyy-MM-dd" (UTC).
     */
    @NonNull
    public static String convertInstantToDate(@NonNull Instant instantToConvert) {
        return FORMATTER_DATE.format(instantToConvert);
    }

    @NonNull
    public static Instant convertDateToInstant(@NonNull String apiDate) {
        TemporalAccessor date = FORMATTER_DATE.parse(apiDate);
        return Instant.from(date);
    }

    public static String convertToLocalTimeOfDay(@NonNull Instant timestamp) {

        // This formatter is not held statically because userTimezone can change.
        DateTimeFormatter localTimeConverter = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withLocale(Locale.UK)
                .withZone(userTimezone);

        return localTimeConverter.format(timestamp);
    }

    /**
     * @param instant
     *         The timestamp to convert to string.
     *
     * @return Current time in format "yyyy-MM-dd HH:mm:ss" (UTC).
     */
    @NonNull
    public static String convertInstantToTimestamp(@NonNull Instant instant) {
        return FORMATTER_DATETIME.format(instant);
    }

    /**
     * @param instantToConvert
     *         The time to convert to SQL format.
     * @param defaultValue
     *         Value to use if [instantToConvert] is <code>null</code>.
     *
     * @return String representation fo the instant with format "yyyy-MM-dd HH:mm:ss"
     */
    public static String convertToSqlFormat(@Nullable Instant instantToConvert,
            String defaultValue) {
        if (instantToConvert == null) {
            return defaultValue;
        }
        else {
            return convertInstantToTimestamp(instantToConvert);
        }
    }

    /**
     * @param timestamp
     *         a timestamp formatted as "yyyy-MM-dd HH:mm:ss" UTC.
     *
     * @return The timestamp converted to an {@link Instant}.
     */
    @NonNull
    public static Instant convertTimestampToInstant(@NonNull String timestamp) {
        TemporalAccessor moment = FORMATTER_DATETIME.parse(timestamp);
        return Instant.from(moment);
    }

    public static void setUserTimeZone(ZoneId timezone) {
        userTimezone = timezone;
    }

    public static boolean isApproximatelyEqual(Instant time1, Instant time2) {
        return isApproximatelyEqual(time1, time2, DEFAULT_APPROX_EQUAL_THRESHOLD_MS);
    }

    public static boolean isApproximatelyEqual(Instant time1, Instant time2, long thresholdMillis) {
        long delta = Math.abs(time1.toEpochMilli() - time2.toEpochMilli());
        return delta < thresholdMillis;
    }

    public static class UnitConverter {

        public static long daysToSeconds(int days) {
            return days * 24 * 60 * 60;
        }

        public static long secondsToMillis(int seconds) {
            return seconds * 1000L;
        }

    }
}
