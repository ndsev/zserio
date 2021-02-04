package zserio.extension.java;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.tools.ExtensionParameters;

public class JavaExtensionParameters
{
    public JavaExtensionParameters(ExtensionParameters parameters)
    {
        outputDir = parameters.getCommandLineArg(OptionJava);
        withWriterCode = parameters.getWithWriterCode();
        withPubsubCode = parameters.getWithPubsubCode();
        withServiceCode = parameters.getWithServiceCode();
        withSqlCode = parameters.getWithSqlCode();
        withValidationCode = parameters.getWithValidationCode();
        withRangeCheckCode = parameters.getWithRangeCheckCode();
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

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
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
        Option option = new Option(OptionJava, true, "generate Java sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    static boolean hasOptionJava(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionJava);
    }

    private static final String OptionJava = "java";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean ignoreTimestamps;
    private final long lastModifiedSourceTime;
}
