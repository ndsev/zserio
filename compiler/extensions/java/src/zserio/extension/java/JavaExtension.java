package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.extension.common.CompatibilityChecker;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ReservedKeywordsClashChecker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

/**
 * The extension which generates Java API sources.
 */
public final class JavaExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Java Generator";
    }

    @Override
    public String getExtensionVersion()
    {
        return JavaExtensionVersion.JAVA_EXTENSION_VERSION_STRING +
                " (BIN " + JavaExtensionVersion.BIN_VERSION_STRING +
                ", JSON " + JavaExtensionVersion.JSON_VERSION_STRING + ")";
    }

    @Override
    public String getZserioVersion()
    {
        return JavaExtensionVersion.EXPECTED_ZSERIO_VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        JavaExtensionParameters.registerOptions(options);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return JavaExtensionParameters.hasOptionJava(parameters);
    }

    @Override
    public void check(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
        rootNode.walk(compatibilityChecker);

        final ReservedKeywordsClashChecker javaKeywordsClashChecker =
                new ReservedKeywordsClashChecker("Java", JAVA_KEYWORDS);
        rootNode.walk(javaKeywordsClashChecker);

        final JavaInnerClassesClashChecker innerClassesClashChecker = new JavaInnerClassesClashChecker();
        rootNode.walk(innerClassesClashChecker);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final OutputFileManager outputFileManager = new OutputFileManager(parameters);
        final JavaExtensionParameters javaParameters = new JavaExtensionParameters(parameters);

        final PackedTypesCollector packedTypesCollector = new PackedTypesCollector();
        rootNode.accept(packedTypesCollector);

        final List<JavaDefaultEmitter> emitters = new ArrayList<JavaDefaultEmitter>();
        emitters.add(new BitmaskEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new EnumerationEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new StructureEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new ChoiceEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new UnionEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new SqlDatabaseEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new SqlTableEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new ConstEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new ServiceEmitter(outputFileManager, javaParameters, packedTypesCollector));
        emitters.add(new PubsubEmitter(outputFileManager, javaParameters, packedTypesCollector));

        // emit Java code
        for (JavaDefaultEmitter emitter: emitters)
            rootNode.walk(emitter);

        outputFileManager.printReport();
    }

    private static final String[] JAVA_KEYWORDS =
    {
        "abstract",   "assert",       "boolean",    "break",    "byte",      "case",
        "catch",      "char",         "class",      "const",    "continue",  "default",
        "double",     "do",           "else",       "enum",     "extends",   "false",
        "final",      "finally",      "float",      "for",      "goto",      "if",
        "implements", "import",       "instanceof", "int",      "interface", "long",
        "native",     "new",          "null",       "package",  "private",   "protected",
        "public",     "return",       "short",      "static",   "strictfp",  "super",
        "switch",     "synchronized", "this",       "throw",    "throws",    "transient",
    };
}
