package zserio.tools;

/**
 * The basic interface for all parameters which are passed into the Zserio extensions.
 */
public interface Parameters
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
     * @returns The file name of the initial Zserio file.
     */
    public String getFileName();

    /**
     * Gets the pathname to Zserio source files.
     *
     * @returns The pathname to Zserio source files.
     */
    public String getPathName();

    /**
     * Gets the list of top level package names.
     *
     * @return The list of top level package names or empty list if no top level package name is specified.
     */
    public Iterable<String> getTopLevelPackageNameList();

    /**
     * Gets the inspector code flag.
     *
     * @returns True if code for Blob inspector is enabled.
     */
    public boolean getWithInspectorCode();

    /**
     * Gets the range check code flag.
     *
     * @returns True if range checking is enabled.
     */
    public boolean getWithRangeCheckCode();

    /**
     * Gets the sources amalgamation flag.
     *
     * @return True if amalgamated sources should be generated.
     */
    public boolean getWithSourcesAmalgamation();

    /**
     * Gets the SQL commands code flag.
     *
     * @returns True if code for SQL commands is enabled.
     */
    public boolean getWithSqlCode();

    /**
     * Gets the GRPC code flag.
     */
    public boolean getWithGrpcCode();

    /**
     * Gets the include validation flag.
     *
     * @returns True if validation is enabled.
     */
    public boolean getWithValidationCode();

    /**
     * Gets the writer code flag.
     *
     * @returns True if writer code is enabled.
     */
    public boolean getWithWriterCode();
}
