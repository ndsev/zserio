package zserio.extension.python;

import java.util.StringJoiner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

/**
 * Command line parameters for Python extension.
 *
 * The class holds all command line parameters passed by core to the Python extension, which are really
 * used by Python emitters.
 */
class PythonExtensionParameters
{
    public PythonExtensionParameters(ExtensionParameters parameters) throws ZserioExtensionException
    {
        outputDir = parameters.getCommandLineArg(OptionNamePython);
        withWriterCode = parameters.getWithWriterCode();
        withPubsubCode = parameters.getWithPubsubCode();
        withServiceCode = parameters.getWithServiceCode();
        withSqlCode = parameters.getWithSqlCode();
        withRangeCheckCode = parameters.getWithRangeCheckCode();
        withTypeInfoCode = parameters.getWithTypeInfoCode();
        withCodeComments = parameters.getWithCodeComments();

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
        if (withTypeInfoCode)
            description.add("typeInfoCode");
        if (withCodeComments)
            description.add("codeComments");
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

    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCode;
    }

    public boolean getWithCodeComments()
    {
        return withCodeComments;
    }

    public String getParametersDescription()
    {
        return parametersDescription;
    }

    static void registerOptions(Options options)
    {
        Option optionPython = new Option(OptionNamePython, true, "generate Python sources");
        optionPython.setArgName("outputDir");
        optionPython.setRequired(false);
        options.addOption(optionPython);
    }

    static boolean hasOptionPython(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionNamePython);
    }

    final static String OptionNamePython = "python";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withRangeCheckCode;
    private final boolean withTypeInfoCode;
    private final boolean withCodeComments;
    private final String parametersDescription;
}
