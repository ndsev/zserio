package zserio.tools;

import java.util.List;

/**
 * The interface which handles all parameters for Zserio extensions.
 */
public interface ExtensionParameters
{
    /**
     * Checks if given command line argument exists.
     *
     * @param argumentName Name of the argument to check.
     *
     * @return true if command line argument is present, false if not.
     */
    public boolean argumentExists(String argumentName);

    /**
     * This method returns the value of a specific command line argument.
     *
     * @param argumentName Name of the argument to get the value from.
     *
     * @return Returns the value of the argument to a given command line argument.
     */
    public String getCommandLineArg(String argumentName);

    /**
     * Gets the file name of the initial Zserio file.
     *
     * @return The file name of the initial Zserio file.
     */
    public String getFileName();

    /**
     * Gets the pathname to Zserio source files.
     *
     * @return The pathname to Zserio source files.
     */
    public String getPathName();

    /**
     * Gets the range check code flag.
     *
     * @return True if range checking is enabled.
     */
    public boolean getWithRangeCheckCode();

    /**
     * Gets the Pub/Sub code flag.
     *
     * @return True when generation of pub/sub code is enabled.
     */
    public boolean getWithPubsubCode();

    /**
     * Gets the service code flag.
     *
     * @return True when generation of service code is enabled.
     */
    public boolean getWithServiceCode();

    /**
     * Gets the SQL commands code flag.
     *
     * @return True if code for SQL commands is enabled.
     */
    public boolean getWithSqlCode();

    /**
     * Gets the type info code flag.
     *
     * @return True if type info code is enabled.
     */
    public boolean getWithTypeInfoCode();

    /**
     * Gets the include validation flag.
     *
     * @return True if validation code is enabled.
     */
    public boolean getWithValidationCode();

    /**
     * Gets the writer code flag.
     *
     * @return True if writer code is enabled.
     */
    public boolean getWithWriterCode();

    /**
     * Gets the list of top level package names ids.
     *
     * @return The list of top level package names ids or empty list if no top level package name is specified.
     */
    public List<String> getTopLevelPackageNameIds();

    /**
     * Gets whether to ignore timestamps and thus always regenerate output.
     *
     * @return True if timestamps should be ignored.
     */
    public boolean getIgnoreTimestamps();

    /**
     * Gets last modified timestamp of last modified Zserio source.
     *
     * @return Last modified timestamp (in milliseconds since epoch).
     */
    public long getLastModifiedTime();
}
