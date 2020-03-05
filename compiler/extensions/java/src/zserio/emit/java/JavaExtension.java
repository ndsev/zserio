package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.Emitter;
import zserio.tools.Extension;
import zserio.tools.Parameters;

/**
 * The extension which generates Java API sources.
 */
public class JavaExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Java Generator";
    }

    @Override
    public String getVersion()
    {
        return JavaExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        Option option = new Option(OptionJava, true, "generate Java sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public boolean isEnabled(Parameters parameters)
    {
        return parameters.argumentExists(OptionJava);
    }

    @Override
    public void generate(Parameters extensionParameters, Root rootNode) throws ZserioEmitException
    {
        final String outputDir = extensionParameters.getCommandLineArg(OptionJava);
        final JavaExtensionParameters javaParameters = new JavaExtensionParameters(outputDir);
        generateJavaSources(extensionParameters, javaParameters, rootNode);
    }

    private void generateJavaSources(Parameters extensionParameters, JavaExtensionParameters javaParameters,
            Root rootNode) throws ZserioEmitException
    {
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new BitmaskEmitter(extensionParameters, javaParameters));
        emitters.add(new EnumerationEmitter(extensionParameters, javaParameters));
        emitters.add(new StructureEmitter(extensionParameters, javaParameters));
        emitters.add(new ChoiceEmitter(extensionParameters, javaParameters));
        emitters.add(new UnionEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlDatabaseEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlTableEmitter(extensionParameters, javaParameters));
        emitters.add(new ConstEmitter(extensionParameters, javaParameters));
        emitters.add(new ServiceEmitter(extensionParameters, javaParameters));
        emitters.add(new PubsubEmitter(extensionParameters, javaParameters));

        // emit Java code
        for (Emitter javaEmitter: emitters)
            rootNode.emit(javaEmitter);
    }

    private static final String OptionJava = "java";
}
