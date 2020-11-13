package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

/**
 * The extension which generates Python API sources.
 */
public class PythonExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Python Generator";
    }

    @Override
    public String getVersion()
    {
        return PythonExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        Option option = new Option(OptionPython, true, "generate Python sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionPython);
    }

    @Override
    public void generate(ExtensionParameters parameters, Root rootNode) throws ZserioExtensionException
    {
        final String outputDir = parameters.getCommandLineArg(OptionPython);

        final List<PythonDefaultEmitter> emitters = new ArrayList<PythonDefaultEmitter>();
        emitters.add(new ConstEmitter(outputDir, parameters));
        emitters.add(new EnumerationEmitter(outputDir, parameters));
        emitters.add(new BitmaskEmitter(outputDir, parameters));
        emitters.add(new SubtypeEmitter(outputDir, parameters));
        emitters.add(new InitPyEmitter(outputDir, parameters));
        emitters.add(new ApiEmitter(outputDir, parameters));
        emitters.add(new StructureEmitter(outputDir, parameters));
        emitters.add(new ChoiceEmitter(outputDir, parameters));
        emitters.add(new UnionEmitter(outputDir, parameters));
        emitters.add(new SqlTableEmitter(outputDir, parameters));
        emitters.add(new SqlDatabaseEmitter(outputDir, parameters));
        emitters.add(new ServiceEmitter(outputDir, parameters));
        emitters.add(new PubsubEmitter(outputDir, parameters));

        // emit Python code
        for (PythonDefaultEmitter pythonEmitter: emitters)
            rootNode.walk(pythonEmitter);
    }

    private final static String OptionPython = "python";
}
