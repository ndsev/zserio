package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.extension.common.CompatibilityChecker;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ReservedKeywordsClashChecker;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.TemplateDataContext.ContextParameters;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

/**
 * The extension which generates C++ API sources.
 */
public final class CppExtension implements Extension
{
    @Override
    public String getName()
    {
        return "C++11 Generator";
    }

    @Override
    public String getExtensionVersion()
    {
        return CppExtensionVersion.CPP_EXTENSION_VERSION_STRING + " (BIN " +
                CppExtensionVersion.BIN_VERSION_STRING + ", JSON " + CppExtensionVersion.JSON_VERSION_STRING +
                ")";
    }

    @Override
    public String getZserioVersion()
    {
        return CppExtensionVersion.EXPECTED_ZSERIO_VERSION_STRING;
    }

    @Override
    public void registerOptions(Options options)
    {
        CppExtensionParameters.registerOptions(options);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return CppExtensionParameters.hasOptionCpp(parameters);
    }

    @Override
    public void check(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        CppExtensionParameters.check(parameters);

        final CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
        rootNode.walk(compatibilityChecker);

        final ReservedKeywordsClashChecker cppKeywordsClashChecker =
                new ReservedKeywordsClashChecker("C++", CPP_KEYWORDS);
        rootNode.walk(cppKeywordsClashChecker);

        final CppInnerClassesClashChecker innerClassesClashChecker = new CppInnerClassesClashChecker();
        rootNode.walk(innerClassesClashChecker);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final OutputFileManager outputFileManager = new OutputFileManager(parameters);
        final CppExtensionParameters cppParameters = new CppExtensionParameters(parameters);

        // collect which types are used within a packed array
        final PackedTypesCollector packedTypesCollector = new PackedTypesCollector();
        rootNode.accept(packedTypesCollector);

        // collect fields which are used as parameters
        final ParameterFieldsCollector parameterFieldsCollector = new ParameterFieldsCollector();
        rootNode.accept(parameterFieldsCollector);

        final ContextParameters contextParameters =
                new ContextParameters(packedTypesCollector, parameterFieldsCollector);

        final List<CppDefaultEmitter> emitters = new ArrayList<CppDefaultEmitter>();
        emitters.add(new ConstEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new SubtypeEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new EnumerationEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new BitmaskEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new StructureEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new ChoiceEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new UnionEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new SqlDatabaseEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new SqlTableEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new ServiceEmitter(outputFileManager, cppParameters, contextParameters));
        emitters.add(new PubsubEmitter(outputFileManager, cppParameters, contextParameters));

        // emit C++ code
        for (CppDefaultEmitter emitter : emitters)
            rootNode.walk(emitter);

        outputFileManager.printReport();
    }

    private static final String[] CPP_KEYWORDS = new String[] {
            "alignas",
            "alignof",
            "and",
            "and_eq",
            "asm",
            "auto",
            "bitand",
            "bitor",
            "bool",
            "break",
            "case",
            "catch",
            "char",
            "char16_t",
            "char32_t",
            "class",
            "compl",
            "const",
            "constexpr",
            "const_cast",
            "continue",
            "decltype",
            "default",
            "delete",
            "do",
            "double",
            "dynamic_cast",
            "else",
            "enum",
            "explicit",
            "export",
            "extern",
            "false",
            "float",
            "for",
            "friend",
            "goto",
            "if",
            "inline",
            "int",
            "long",
            "mutable",
            "namespace",
            "new",
            "noexcept",
            "not",
            "not_eq",
            "nullptr",
            "operator",
            "or",
            "or_eq",
            "private",
            "protected",
            "public",
            "register",
            "reinterpret_cast",
            "return",
            "short",
            "signed",
            "sizeof",
            "static",
            "static_assert",
            "static_cast",
            "struct",
            "switch",
            "template",
            "this",
            "thread_local",
            "throw",
            "true",
            "try",
            "typedef",
            "typeid",
            "typename",
            "union",
            "unsigned",
            "using",
            "virtual",
            "void",
            "volatile",
            "wchar_t",
            "while",
            "xor",
            "xor_eq",
    };
}
