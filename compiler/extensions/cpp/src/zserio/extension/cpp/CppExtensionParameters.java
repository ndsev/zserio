package zserio.extension.cpp;

import java.util.StringJoiner;

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

        final StringJoiner description = new StringJoiner(", ");
        if (withWriterCode)
            description.add("writerCode");
        if (withPubsubCode)
            description.add("pubsubCode");
        if (withServiceCode)
            description.add("serviceCode");
        if (withSqlCode)
            description.add("sqlCode");
        if (withRangeCheckCode)
            description.add("rangeCheckCode");
        if (withSourcesAmalgamation)
            description.add("sourcesAmalgamation");
        parametersDescription = description.toString();
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

    public String getParametersDescription()
    {
        return parametersDescription;
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
    private final String parametersDescription;
}
