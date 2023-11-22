package zserio.extension.python;

/**
 * Python extension version information.
 *
 * Python extension version will be reported to Zserio core and will be stored in MANIFEST.MF.
 *
 * Expected Zserio core version will be used to check if the extension can be safely loaded.
 */
class PythonExtensionVersion
{
    /** Python extension version string. */
    public static final String PYTHON_EXTENSION_VERSION_STRING = "1.0.0";

    /** Expected Zserio core version string. */
    public static final String EXPECTED_ZSERIO_VERSION_STRING = "2.12.0";
}
