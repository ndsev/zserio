package zserio.extension.java;

/**
 * Java extension version information.
 *
 * Java extension version will be reported to Zserio core and will be stored in MANIFEST.MF.
 *
 * Expected Zserio core version will be used to check if the extension can be safely loaded.
 */
public final class JavaExtensionVersion
{
    /** Java extension version string. */
    public static final String JAVA_EXTENSION_VERSION_STRING = "1.1.0";

    /** Expected Zserio core version string. */
    public static final String EXPECTED_ZSERIO_VERSION_STRING = "2.14.0";

    /** Zserio BIN version string. */
    public static final String BIN_VERSION_STRING = "1.0";

    /** Zserio JSON version string. */
    public static final String JSON_VERSION_STRING = "1.0";
}
