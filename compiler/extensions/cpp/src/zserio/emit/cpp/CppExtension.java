package zserio.emit.cpp;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Extension;
import zserio.tools.Parameters;

/**
 * The extension which generates C++ API sources.
 */
public class CppExtension implements Extension
{
    @Override
    public String getName()
    {
        return "C++11 Generator";
    }

    @Override
    public String getVersion()
    {
        return CppExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        if (!options.hasOption(OptionCpp))
        {
            Option option = new Option(OptionCpp, true, "generate C++ sources");
            option.setArgName("outputDir");
            option.setRequired(false);
            options.addOption(option);
        }
    }

    @Override
    public boolean isEnabled(Parameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
    }

    @Override
    public void generate(Parameters parameters, Root rootNode) throws ZserioEmitException
    {
        final String outputDir = parameters.getCommandLineArg(OptionCpp);

        // emit C++ code
        rootNode.emit(new ConstEmitter(outputDir, parameters));
        rootNode.emit(new SubtypeEmitter(outputDir, parameters));
        rootNode.emit(new EnumerationEmitter(outputDir, parameters));
        rootNode.emit(new BitmaskEmitter(outputDir, parameters));
        rootNode.emit(new StructureEmitter(outputDir, parameters));
        rootNode.emit(new ChoiceEmitter(outputDir, parameters));
        rootNode.emit(new UnionEmitter(outputDir, parameters));
        rootNode.emit(new SqlDatabaseEmitter(outputDir, parameters));
        rootNode.emit(new SqlTableEmitter(outputDir, parameters));
        rootNode.emit(new ServiceEmitter(outputDir, parameters));
        rootNode.emit(new PubsubEmitter(outputDir, parameters));
    }

    private final static String OptionCpp = "cpp";
}
