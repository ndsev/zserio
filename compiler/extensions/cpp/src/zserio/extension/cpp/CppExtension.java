package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

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
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final String outputDir = parameters.getCommandLineArg(OptionCpp);

        final List<CppDefaultEmitter> emitters = new ArrayList<CppDefaultEmitter>();
        emitters.add(new ConstEmitter(outputDir, parameters));
        emitters.add(new SubtypeEmitter(outputDir, parameters));
        emitters.add(new EnumerationEmitter(outputDir, parameters));
        emitters.add(new BitmaskEmitter(outputDir, parameters));
        emitters.add(new StructureEmitter(outputDir, parameters));
        emitters.add(new ChoiceEmitter(outputDir, parameters));
        emitters.add(new UnionEmitter(outputDir, parameters));
        emitters.add(new SqlDatabaseEmitter(outputDir, parameters));
        emitters.add(new SqlTableEmitter(outputDir, parameters));
        emitters.add(new ServiceEmitter(outputDir, parameters));
        emitters.add(new PubsubEmitter(outputDir, parameters));

        // emit C++ code
        for (CppDefaultEmitter emitter: emitters)
            rootNode.walk(emitter);
    }

    private final static String OptionCpp = "cpp";
}
