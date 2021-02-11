package zserio.extension.java;

import java.util.StringJoiner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.tools.ExtensionParameters;

/**
 * Command line parameters for Java extension.
 *
 * The class holds all command line parameters passed by core to the Java extension, which are really
 * used by Java emitters.
 */
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

        final StringJoiner description = new StringJoiner(", ");
        if (withWriterCode)
            description.add("writerCode");
        if (withPubsubCode)
            description.add("pubsubCode");
        if (withServiceCode)
            description.add("serviceCode");
        if (withSqlCode)
            description.add("sqlCode");
        if (withValidationCode)
            description.add("validationCode");
        if (withRangeCheckCode)
            description.add("rangeCheckCode");
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

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public String getParametersDescription()
    {
        return parametersDescription;
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
    private final String parametersDescription;
}
