package zserio.extension.xml;

/**
 * XML extension version information.
 *
 * XML extension version will be reported to Zserio core and will be stored in MANIFEST.MF.
 *
 * Expected Zserio core version will be used to check if the extension can be safely loaded.
 */
public final class XmlExtensionVersion
{
    /** XML extension version string. */
    public static final String XML_EXTENSION_VERSION_STRING = "1.0.5";

    /** Expected Zserio core version string. */
    public static final String EXPECTED_ZSERIO_VERSION_STRING = "2.16.1";
}
