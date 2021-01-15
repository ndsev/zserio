package zserio.extension.python;

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
        withPythonPropPrefix = !parameters.argumentExists(OptionWithoutPythonPropPrefix);
        withRangeCheckCode = parameters.getWithRangeCheckCode();
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

    public boolean getWithPythonPropPrefix()
    {
        return withPythonPropPrefix;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    static void registerOptions(Options options)
    {
        Option optionPython = new Option(OptionPython, true, "generate Python sources");
        optionPython.setArgName("outputDir");
        optionPython.setRequired(false);
        options.addOption(optionPython);

        OptionGroup pythonPropPrefixGroup = new OptionGroup();
        Option option = new Option(OptionWithPythonPropPrefix, false,
                "add 'prop' prefix to python properties (default)");
        pythonPropPrefixGroup.addOption(option);
        option = new Option(OptionWithoutPythonPropPrefix, false,
                "don't add 'prop' prefix to python properties (warn: possibly cause name clashing!)");
        pythonPropPrefixGroup.addOption(option);
        pythonPropPrefixGroup.setRequired(false);
        options.addOptionGroup(pythonPropPrefixGroup);
    }

    static boolean hasOptionPython(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionPython);
    }

    final static String OptionPython = "python";
    final static String OptionWithPythonPropPrefix = "withPythonPropPrefix";
    final static String OptionWithoutPythonPropPrefix = "withoutPythonPropPrefix";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withPythonPropPrefix;
    private final boolean withRangeCheckCode;
}