package zserio.tools;

import java.util.List;

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
    public boolean getWithRangeCheckCode()
    {
        return commandLineArguments.getWithRangeCheckCode();
    }

    @Override
    public boolean getWithPubsubCode()
    {
        return commandLineArguments.getWithPubsubCode();
    }

    @Override
    public boolean getWithServiceCode()
    {
        return commandLineArguments.getWithServiceCode();
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
    public List<String> getTopLevelPackageNameIds()
    {
        return commandLineArguments.getTopLevelPackageNameIds();
    }

    private final CommandLineArguments commandLineArguments;
}
