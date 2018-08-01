package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import antlr.RecognitionException;
import zserio.antlr.ZserioEmitter;
import zserio.ast.TokenAST;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.Emitter;
import zserio.tools.Extension;
import zserio.tools.Parameters;

/**
 * The extension which generates C++ API sources.
 */
public class CppExtension implements Extension
{
    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return "C++ Generator";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion()
    {
        return CppExtensionVersion.VERSION_STRING;
    }

    /** {@inheritDoc} */
    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        Option option = new Option(OptionCpp, true, "generate C++ sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    /** {@inheritDoc} */
    @Override
    public void generate(Parameters parameters, ZserioEmitter emitter, TokenAST rootNode)
        throws ZserioEmitException
    {
        if (!parameters.argumentExists(OptionCpp))
        {
            System.out.println("Emitting C++ files is disabled");
            return;
        }

        System.out.println("Emitting C++ code");
        generateCppSources(parameters, emitter, rootNode);
    }

    private void generateCppSources(Parameters parameters, ZserioEmitter emitter, TokenAST rootNode)
            throws ZserioEmitException
    {
        final String outputDir = parameters.getCommandLineArg(OptionCpp);
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new MasterDatabaseEmitter(outputDir, parameters));
        emitters.add(new SqlDatabaseEmitter(outputDir, parameters));
        emitters.add(new ParameterProviderEmitter(outputDir, parameters));
        emitters.add(new SqlTableEmitter(outputDir, parameters));
        emitters.add(new SqlDatabaseInspectorEmitter(outputDir, parameters));
        emitters.add(new SqlTableInspectorEmitter(outputDir, parameters));
        emitters.add(new InspectorParameterProviderEmitter(outputDir, parameters));
        emitters.add(new InspectorZserioNamesEmitter(outputDir, parameters));
        emitters.add(new InspectorZserioTypeNamesEmitter(outputDir, parameters));
        emitters.add(new ConstEmitter(outputDir, parameters));
        emitters.add(new SubtypeEmitter(outputDir, parameters));
        emitters.add(new EnumerationEmitter(outputDir, parameters));
        emitters.add(new RpcEmitter(outputDir, parameters));
        emitters.add(new StructureEmitter(outputDir, parameters));
        emitters.add(new ChoiceEmitter(outputDir, parameters));
        emitters.add(new UnionEmitter(outputDir, parameters));

        try
        {
            // emit C++ code for decoders
            for (Emitter cppEmitter: emitters)
            {
                emitter.setEmitter(cppEmitter);
                emitter.root(rootNode);
            }
        }
        catch (RecognitionException exception)
        {
            System.out.println("CppExtension exception:" + exception);
            throw new ZserioEmitException(exception);
        }
    }

    private final static String OptionCpp = "cpp";
}
