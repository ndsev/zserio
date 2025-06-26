package zserio.extension.doc;

/**
 * Documentation extensions version information.
 *
 * Documentation extension version will be reported to Zserio core and will be stored in MANIFEST.MF.
 *
 * Expected Zserio core version will be used to check if the extension can be safely loaded.
 */
final class DocExtensionVersion
{
    /** Documentation extension version string. */
    public static final String DOC_EXTENSION_VERSION_STRING = "1.0.4";

    /** Expected Zserio core version string. */
    public static final String EXPECTED_ZSERIO_VERSION_STRING = "2.16.1";
}
