package zserio.tools;

import java.util.List;

/**
 * The class which handles all parameters for Zserio extensions.
 */
public class ExtensionParameters
{
    /**
     * Constructor from command line arguments and Zserio parser.
     *
     * @param commandLineArguments Command line arguments to construct from.
     */
    public ExtensionParameters(CommandLineArguments commandLineArguments)
    {
        this.commandLineArguments = commandLineArguments;
    }

    /**
     * Checks if given command line argument exists.
     *
     * @param argumentName Name of the argument to check.
     *
     * @return true if command line argument is present, false if not.
     */
    public boolean argumentExists(String argumentName)
    {
        return commandLineArguments.hasOption(argumentName);
    }

    /**
     * This method returns the value of a specific command line argument.
     *
     * @param argumentName Name of the argument to get the value from.
     *
     * @return Returns the value of the argument to a given command line argument.
     */
    public String getCommandLineArg(String argumentName)
    {
        return commandLineArguments.getOptionValue(argumentName);
    }

    /**
     * Gets the file name of the initial Zserio file.
     *
     * @returns The file name of the initial Zserio file.
     */
    public String getFileName()
    {
        return commandLineArguments.getInputFileName();
    }

    /**
     * Gets the pathname to Zserio source files.
     *
     * @returns The pathname to Zserio source files.
     */
    public String getPathName()
    {
        return commandLineArguments.getSrcPathName();
    }

    /**
     * Gets the range check code flag.
     *
     * @returns True if range checking is enabled.
     */
    public boolean getWithRangeCheckCode()
    {
        return commandLineArguments.getWithRangeCheckCode();
    }

    /**
     * Gets the Pub/Sub code flag.
     */
    public boolean getWithPubsubCode()
    {
        return commandLineArguments.getWithPubsubCode();
    }

    /**
     * Gets the service code flag.
     */
    public boolean getWithServiceCode()
    {
        return commandLineArguments.getWithServiceCode();
    }

    /**
     * Gets the sources amalgamation flag.
     *
     * @return True if amalgamated sources should be generated.
     */
    public boolean getWithSourcesAmalgamation()
    {
        return commandLineArguments.getWithSourcesAmalgamation();
    }

    /**
     * Gets the SQL commands code flag.
     *
     * @returns True if code for SQL commands is enabled.
     */
    public boolean getWithSqlCode()
    {
        return commandLineArguments.getWithSqlCode();
    }

    /**
     * Gets the include validation flag.
     *
     * @returns True if validation is enabled.
     */
    public boolean getWithValidationCode()
    {
        return commandLineArguments.getWithValidationCode();
    }

    /**
     * Gets the writer code flag.
     *
     * @returns True if writer code is enabled.
     */
    public boolean getWithWriterCode()
    {
        return commandLineArguments.getWithWriterCode();
    }

    /**
     * Gets the list of top level package names ids.
     *
     * @return The list of top level package names ids or empty list if no top level package name is specified.
     */
    public List<String> getTopLevelPackageNameIds()
    {
        return commandLineArguments.getTopLevelPackageNameIds();
    }

    private final CommandLineArguments commandLineArguments;
}
