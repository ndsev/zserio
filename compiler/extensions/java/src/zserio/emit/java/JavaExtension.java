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

        option = new Option(OptionJavaVersion, true,
                "force Java version for generated sources (e.g. 1.6 or 6)");
        option.setArgName("version");
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
        final String javaVersionOption = (!extensionParameters.argumentExists(OptionJavaVersion)) ? null :
            extensionParameters.getCommandLineArg(OptionJavaVersion);
        final String javaMajorVersion = getMajorVersion(javaVersionOption);

        // check if Java version is at least 6 (Zserio cannot generate code for older versions)
        try
        {
            if (Integer.parseInt(javaMajorVersion) < Integer.parseInt(JAVA_DEFAULT_MAJOR_VERSION))
                throw new ZserioEmitException("Can't generate Java code for version " + javaMajorVersion +
                        "!");
        }
        catch (NumberFormatException exception)
        {
            throw new ZserioEmitException("Invalid Java version " + javaMajorVersion + "!");
        }

        final String outputDir = extensionParameters.getCommandLineArg(OptionJava);
        final JavaExtensionParameters javaParameters = new JavaExtensionParameters(outputDir, javaMajorVersion);
        generateJavaSources(extensionParameters, javaParameters, rootNode);
    }

    private void generateJavaSources(Parameters extensionParameters, JavaExtensionParameters javaParameters,
            Root rootNode) throws ZserioEmitException
    {
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new EnumerationEmitter(extensionParameters, javaParameters));
        emitters.add(new ServiceEmitter(extensionParameters, javaParameters));
        emitters.add(new StructureEmitter(extensionParameters, javaParameters));
        emitters.add(new ChoiceEmitter(extensionParameters, javaParameters));
        emitters.add(new UnionEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlDatabaseEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlTableEmitter(extensionParameters, javaParameters));
        emitters.add(new ConstEmitter(extensionParameters, javaParameters));

        // emit Java code
        for (Emitter javaEmitter: emitters)
            rootNode.emit(javaEmitter);
    }

    private static String getMajorVersion(String javaVersionOption)
    {
        String majorVersion = JAVA_DEFAULT_MAJOR_VERSION;
        final String javaVersion = (javaVersionOption != null) ? javaVersionOption :
            System.getProperty("java.version");
        final String[] javaVersionElements = javaVersion.split("\\.");
        if (javaVersionElements.length > 0)
        {
            final String firstVersionElement = javaVersionElements[0];
            if (firstVersionElement.equals(JAVA_OLD_VERSION_BEFORE_MAJOR))
            {
                // old Java versions (before 9) have the first version element equal to '1'
                if (javaVersionElements.length > 1)
                    majorVersion = javaVersionElements[1];
            }
            else
            {
                // new Java versions (after 8) have the first version element equal to major version
                majorVersion = firstVersionElement;
            }
        }

        return majorVersion;
    }

    private static final String JAVA_OLD_VERSION_BEFORE_MAJOR = "1";
    private static final String JAVA_DEFAULT_MAJOR_VERSION = "6";

    private static final String OptionJava = "java";
    private static final String OptionJavaVersion = "javaVersion";
}
