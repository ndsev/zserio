package zserio.tools;

/**
 * Defines which extensions versions are usable by current zserio core.
 */
class ExtensionVersionMatcher
{
    /**
     * Check if the given extension can be used.
     *
     * @param zserioVersionString Version of the current zserio core.
     * @param extensionVersionString Version of extension to check.
     *
     * @return True when the extension can be used, false otherwise.
     */
    static boolean match(String zserioVersionString, String extensionVersionString)
    {
        try
        {
            final ZserioVersion zserioVersion = ZserioVersion.parseVersion(zserioVersionString);
            final ZserioVersion extensionVersion = ZserioVersion.parseVersion(extensionVersionString);

            return zserioVersion.getMajor() == extensionVersion.getMajor() &&
                    zserioVersion.getMinor() == extensionVersion.getMinor() &&
                    zserioVersion.compareTo(extensionVersion) >= 0;
        }
        catch (RuntimeException e)
        {
            return false;
        }
    }
};
