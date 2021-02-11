package zserio.extension.python;

import java.util.StringJoiner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

/**
 * Command line parameters for python extension.
 *
 * The class holds all command line parameters passed by core to the python extension, which are really
 * used by python emitters.
 */
class PythonExtensionParameters
{
    public PythonExtensionParameters(ExtensionParameters parameters) throws ZserioExtensionException
    {
        outputDir = parameters.getCommandLineArg(OptionPython);
        withWriterCode = parameters.getWithWriterCode();
        withPubsubCode = parameters.getWithPubsubCode();
        withServiceCode = parameters.getWithServiceCode();
        withSqlCode = parameters.getWithSqlCode();
        withPythonProperties = parameters.argumentExists(OptionWithPythonProperties);
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
        if (withPythonProperties)
            description.add("pythonProperties");
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

    public boolean getWithPythonProperties()
    {
        return withPythonProperties;
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
        Option optionPython = new Option(OptionPython, true, "generate Python sources");
        optionPython.setArgName("outputDir");
        optionPython.setRequired(false);
        options.addOption(optionPython);

        OptionGroup pythonPropertiesGroup = new OptionGroup();
        Option option = new Option(OptionWithPythonProperties, false,
                "use python properties instead of getters and setters");
        pythonPropertiesGroup.addOption(option);
        option = new Option(OptionWithoutPythonProperties, false,
                "use getters and setters(default)");
        pythonPropertiesGroup.addOption(option);
        pythonPropertiesGroup.setRequired(false);
        options.addOptionGroup(pythonPropertiesGroup);
    }

    static boolean hasOptionPython(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionPython);
    }

    final static String OptionPython = "python";
    final static String OptionWithPythonProperties = "withPythonProperties";
    final static String OptionWithoutPythonProperties = "withoutPythonProperties";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withPythonProperties;
    private final boolean withRangeCheckCode;
    private final String parametersDescription;
}
