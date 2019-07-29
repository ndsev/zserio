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

        // emit C++ code
        final ServiceEmitter serviceEmitter = new ServiceEmitter(outputDir, parameters);
        rootNode.emit(serviceEmitter);
        rootNode.emit(new ConstEmitter(outputDir, parameters));
        rootNode.emit(new SubtypeEmitter(outputDir, parameters));
        rootNode.emit(new EnumerationEmitter(outputDir, parameters));
        rootNode.emit(new StructureEmitter(outputDir, parameters, serviceEmitter.getRpcTypes()));
        rootNode.emit(new ChoiceEmitter(outputDir, parameters));
        rootNode.emit(new UnionEmitter(outputDir, parameters, serviceEmitter.getRpcTypes()));
    }

    private final static String OptionCpp = "cpp";
    private final static String OptionCppStandard = "cppStandard";
}
