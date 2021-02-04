package zserio.extension.cpp;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.tools.ExtensionParameters;

/**
 * Command line parameters for C++ extension.
 *
 * The class holds all command line parameters passed by core to the C++ extension, which are really
 * used by C++ emitters.
 */
public class CppExtensionParameters
{
    public CppExtensionParameters(ExtensionParameters parameters)
    {
        outputDir = parameters.getCommandLineArg(OptionCpp);
        withWriterCode = parameters.getWithWriterCode();
        withPubsubCode = parameters.getWithPubsubCode();
        withServiceCode = parameters.getWithServiceCode();
        withSqlCode = parameters.getWithSqlCode();
        withRangeCheckCode = parameters.getWithRangeCheckCode();
        withSourcesAmalgamation = parameters.getWithSourcesAmalgamation();
        ignoreTimestamps = parameters.getIgnoreTimestamps();
        lastModifiedSourceTime = parameters.getLastModifiedTime();
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithPubsubCode()
    {
        return withPubsubCode;
    }

    public boolean getWithServiceCode()
    {
        return withServiceCode;
    }

    public boolean getWithSqlCode()
    {
        return withSqlCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getWithSourcesAmalgamation()
    {
        return withSourcesAmalgamation;
    }

    public boolean getIngoreTimestamps()
    {
        return ignoreTimestamps;
    }

    public long getLastModifiedSourceTime()
    {
        return lastModifiedSourceTime;
    }

    static void registerOptions(Options options)
    {
        Option option = new Option(OptionCpp, true, "generate C++ sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    static boolean hasOptionCpp(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
    }

    private final static String OptionCpp = "cpp";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withRangeCheckCode;
    private final boolean withSourcesAmalgamation;
    private final boolean ignoreTimestamps;
    private final long lastModifiedSourceTime;
}
