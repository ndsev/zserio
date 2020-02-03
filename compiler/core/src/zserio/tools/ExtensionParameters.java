package zserio.tools;

import java.util.ArrayList;

/**
 * The class which handles all parameters for extensions.
 */
public class ExtensionParameters implements Parameters
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

    @Override
    public boolean argumentExists(String argumentName)
    {
        return commandLineArguments.hasOption(argumentName);
    }

    @Override
    public String getCommandLineArg(String argumentName)
    {
        return commandLineArguments.getOptionValue(argumentName);
    }

    @Override
    public String getFileName()
    {
        return commandLineArguments.getInputFileName();
    }

    @Override
    public String getPathName()
    {
        return commandLineArguments.getSrcPathName();
    }

    @Override
    public boolean getWithInspectorCode()
    {
        return commandLineArguments.getWithInspectorCode();
    }

    @Override
    public boolean getWithRangeCheckCode()
    {
        return commandLineArguments.getWithRangeCheckCode();
    }

    @Override
    public boolean getWithSourcesAmalgamation()
    {
        return commandLineArguments.getWithSourcesAmalgamation();
    }

    @Override
    public boolean getWithSqlCode()
    {
        return commandLineArguments.getWithSqlCode();
    }

    @Override
    public boolean getWithValidationCode()
    {
        return commandLineArguments.getWithValidationCode();
    }

    @Override
    public boolean getWithWriterCode()
    {
        return commandLineArguments.getWithWriterCode();
    }

    @Override
    public Iterable<String> getTopLevelPackageNameList()
    {
        final String topLevelPackageName = commandLineArguments.getTopLevelPackageName();
        if (topLevelPackageName == null)
            return new ArrayList<String>();

        return java.util.Arrays.asList(topLevelPackageName.split("\\" + TOP_LEVEL_PACKAGE_NAME_SEPARATOR));
    }

    private static final String TOP_LEVEL_PACKAGE_NAME_SEPARATOR = ".";

    private final CommandLineArguments  commandLineArguments;
}
