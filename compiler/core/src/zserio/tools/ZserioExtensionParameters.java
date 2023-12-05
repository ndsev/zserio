package zserio.tools;

import java.util.List;

/**
 * The class for all parameters for Zserio extensions implementing ExtensionParameters interface.
 */
final class ZserioExtensionParameters implements ExtensionParameters
{
    /**
     * Constructor from command line arguments and Zserio parser.
     *
     * @param commandLineArguments Command line arguments to construct from.
     * @param lastModifiedSourceTime Last modified timestamp of last modified Zserio source.
     * @param lastModifiedResourceTime Last modified timestamp of last modified Zserio resource (e.g. jar file).
     */
    public ZserioExtensionParameters(CommandLineArguments commandLineArguments, long lastModifiedSourceTime,
            long lastModifiedResourceTime)
    {
        this.commandLineArguments = commandLineArguments;
        this.lastModifiedTime = (lastModifiedSourceTime == 0L || lastModifiedResourceTime == 0L)
                ? 0L
                : Math.max(lastModifiedSourceTime, lastModifiedResourceTime);
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
    public boolean getWithSqlCode()
    {
        return commandLineArguments.getWithSqlCode();
    }

    @Override
    public boolean getWithTypeInfoCode()
    {
        return commandLineArguments.getWithTypeInfoCode();
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
    public boolean getWithCodeComments()
    {
        return commandLineArguments.getWithCodeComments();
    }

    @Override
    public List<String> getTopLevelPackageNameIds()
    {
        return commandLineArguments.getTopLevelPackageNameIds();
    }

    @Override
    public boolean getIgnoreTimestamps()
    {
        return commandLineArguments.getIgnoreTimestamps();
    }

    @Override
    public long getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    @Override
    public String getZserioVersion()
    {
        return ZserioVersion.VERSION_STRING;
    }

    private final CommandLineArguments commandLineArguments;
    private final long lastModifiedTime;
}
