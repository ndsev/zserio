package zserio.extension.cpp;

/**
 * C++ extension version information.
 *
 * The version will be stored in MANIFEST.MF and is used by ZserioTool to check
 * if the extension can be safely loaded - e.g. the version must match to the ZserioTool version.
 */
public class CppExtensionVersion
{
    /** C++ extension version string. */
    public static final String VERSION_STRING = "2.12.0";

    /** C++ extension version in integer value to allow version checking in generated code. */
    public static final long VERSION_NUMBER = 2012000;
}
