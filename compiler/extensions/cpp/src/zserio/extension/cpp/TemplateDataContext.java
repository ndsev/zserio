package zserio.extension.cpp;

import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ExpressionFormattingPolicy;

final class TemplateDataContext
{
    public TemplateDataContext(CppExtensionParameters cppParameters)
    {
        typesContext = new TypesContext(cppParameters.getAllocatorDefinition());
        cppNativeMapper = new CppNativeMapper(typesContext);

        withWriterCode = cppParameters.getWithWriterCode();
        withRangeCheckCode = cppParameters.getWithRangeCheckCode();
        withValidationCode = cppParameters.getWithValidationCode();

        generatorDescription =
                "/**\n" +
                " * Automatically generated by Zserio C++ extension version " +
                        CppExtensionVersion.VERSION_STRING + ".\n" +
                " * Generator setup: " + cppParameters.getParametersDescription() + ".\n" +
                " */";
    }

    public CppNativeMapper getCppNativeMapper()
    {
        return cppNativeMapper;
    }

    public ExpressionFormatter getExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppExpressionFormattingPolicy(cppNativeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getOwnerIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppOwnerIndirectExpressionFormattingPolicy(cppNativeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getSqlIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppSqlIndirectExpressionFormattingPolicy(cppNativeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public TypesContext getTypesContext()
    {
        return typesContext;
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    private final TypesContext typesContext;

    private final CppNativeMapper cppNativeMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
    private final boolean withValidationCode;
    private final String generatorDescription;
}
