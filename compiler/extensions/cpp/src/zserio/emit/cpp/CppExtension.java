package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.Emitter;
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

        if (!options.hasOption(OptionCppStandard))
        {
            Option option = new Option(OptionCppStandard, true,
                    "use C++ standard for generated sources: c++11");
            option.setArgName("standard");
            option.setRequired(false);
            options.addOption(option);
        }
    }

    @Override
    public boolean isEnabled(Parameters parameters)
    {
        final String cppStandard = parameters.getCommandLineArg(OptionCppStandard);
        final boolean isCppStandard11 = (cppStandard == null) ? true : cppStandard.equals("c++11");

        return parameters.argumentExists(OptionCpp) && isCppStandard11;
    }

    @Override
    public void generate(Parameters parameters, Root rootNode) throws ZserioEmitException
    {
        final String outputDir = parameters.getCommandLineArg(OptionCpp);
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new ConstEmitter(outputDir, parameters));
        emitters.add(new SubtypeEmitter(outputDir, parameters));
        emitters.add(new EnumerationEmitter(outputDir, parameters));
        emitters.add(new StructureEmitter(outputDir, parameters));
        emitters.add(new ChoiceEmitter(outputDir, parameters));

        // emit C++ code
        for (Emitter cppEmitter: emitters)
            rootNode.emit(cppEmitter);
    }

    private final static String OptionCpp = "cpp";
    private final static String OptionCppStandard = "cppStandard";
}
