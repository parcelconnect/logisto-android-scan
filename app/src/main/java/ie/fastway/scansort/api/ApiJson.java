package ie.fastway.scansort.api;

/**
 * Marks JSON classes that are used with the API.
 *
 * Classes marked with this interface should not be obfuscated by ProGuard, because Gson uses
 * the class field names for searialisation.
 *
 */
public interface ApiJson {
}
