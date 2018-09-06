package zserio.emit.java;

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
 * The extension which generates Java API sources.
 */
public class JavaExtension implements Extension
{
    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return "Java Generator";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion()
    {
        return JavaExtensionVersion.VERSION_STRING;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void generate(Parameters extensionParameters, ZserioEmitter emitter, TokenAST rootNode)
        throws ZserioEmitException
    {
        if (!extensionParameters.argumentExists(OptionJava))
        {
            System.out.println("Emitting Java files is disabled");
            return;
        }

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

        System.out.println("Emitting Java" + javaMajorVersion + " code");

        final String outputDir = extensionParameters.getCommandLineArg(OptionJava);
        final JavaExtensionParameters javaParameters = new JavaExtensionParameters(outputDir, javaMajorVersion);
        generateJavaSources(extensionParameters, javaParameters, emitter, rootNode);
    }

    private void generateJavaSources(Parameters extensionParameters, JavaExtensionParameters javaParameters,
            ZserioEmitter emitter, TokenAST rootNode) throws ZserioEmitException
    {
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new EnumerationEmitter(extensionParameters, javaParameters));
        emitters.add(new ServiceEmitter(extensionParameters, javaParameters));
        emitters.add(new StructureEmitter(extensionParameters, javaParameters));
        emitters.add(new ChoiceEmitter(extensionParameters, javaParameters));
        emitters.add(new UnionEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlDatabaseValidatorEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlDatabaseEmitter(extensionParameters, javaParameters));
        emitters.add(new SqlTableEmitter(extensionParameters, javaParameters));
        emitters.add(new ParameterProviderEmitter(extensionParameters, javaParameters));
        emitters.add(new ConstEmitter(extensionParameters, javaParameters));
        emitters.add(new MasterDatabaseEmitter(extensionParameters, javaParameters));

        try
        {
            // emit Java code for decoders
            for (Emitter javaEmitter: emitters)
            {
                emitter.setEmitter(javaEmitter);
                emitter.root(rootNode);
            }
        }
        catch (RecognitionException exception)
        {
            System.out.println("JavaExtension exception:" + exception);
            throw new ZserioEmitException(exception);
        }
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
