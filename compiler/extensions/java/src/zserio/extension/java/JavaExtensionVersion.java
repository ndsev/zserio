package zserio.extension.java;

/**
 * Java extension version information.
 *
 * Java extension version will be reported to Zserio core and will be stored in MANIFEST.MF.
 *
 * Expected Zserio core version will be used to check if the extension can be safely loaded.
 */
public class JavaExtensionVersion
{
    /** Java extension version string. */
    public static final String JAVA_EXTENSION_VERSION_STRING = "1.0.0";

    /** Expected Zserio core version string. */
    public static final String EXPECTED_ZSERIO_VERSION_STRING = "2.12.0";
}
