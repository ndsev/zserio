package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
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
        generateJavaSources(javaParameters, extensionParameters, rootNode);
    }

    private void generateJavaSources(JavaExtensionParameters javaParameters, Parameters extensionParameters,
            Root rootNode) throws ZserioEmitException
    {
        final List<JavaDefaultEmitter> emitters = new ArrayList<JavaDefaultEmitter>();
        emitters.add(new BitmaskEmitter(javaParameters, extensionParameters));
        emitters.add(new EnumerationEmitter(javaParameters, extensionParameters));
        emitters.add(new StructureEmitter(javaParameters, extensionParameters));
        emitters.add(new ChoiceEmitter(javaParameters, extensionParameters));
        emitters.add(new UnionEmitter(javaParameters, extensionParameters));
        emitters.add(new SqlDatabaseEmitter(javaParameters, extensionParameters));
        emitters.add(new SqlTableEmitter(javaParameters, extensionParameters));
        emitters.add(new ConstEmitter(javaParameters, extensionParameters));
        emitters.add(new ServiceEmitter(javaParameters, extensionParameters));
        emitters.add(new PubsubEmitter(javaParameters, extensionParameters));

        // emit Java code
        for (JavaDefaultEmitter emitter: emitters)
            rootNode.walk(emitter);
    }

    private static final String OptionJava = "java";
}
