package zserio.emit.java;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

final class TemplateDataContext
{
    public TemplateDataContext(Parameters extensionParameters, JavaExtensionParameters javaParameters,
            PackageMapper javaPackageMapper)
    {
        javaNativeMapper = new JavaNativeMapper(javaPackageMapper);
        javaRootPackageName = JavaFullNameFormatter.getFullName(javaPackageMapper.getRootPackageName());

        final JavaExpressionFormattingPolicy policy = new JavaExpressionFormattingPolicy(javaNativeMapper);
        javaExpressionFormatter = new ExpressionFormatter(policy);

        final JavaCaseExpressionFormattingPolicy casePolicy =
                new JavaCaseExpressionFormattingPolicy(javaNativeMapper);
        javaCaseExpressionFormatter = new ExpressionFormatter(casePolicy);

        final JavaSqlIndirectExpressionFormattingPolicy sqlIndirectPolicy =
                new JavaSqlIndirectExpressionFormattingPolicy(javaNativeMapper);
        javaSqlIndirectExpressionFormatter = new ExpressionFormatter(sqlIndirectPolicy);

        withWriterCode = extensionParameters.getWithWriterCode();
        withValidationCode = extensionParameters.getWithValidationCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
    }

    public JavaNativeMapper getJavaNativeMapper()
    {
        return javaNativeMapper;
    }

    public String getJavaRootPackageName()
    {
        return javaRootPackageName;
    }

    public ExpressionFormatter getJavaExpressionFormatter()
    {
        return javaExpressionFormatter;
    }

    public ExpressionFormatter getJavaCaseExpressionFormatter()
    {
        return javaCaseExpressionFormatter;
    }

    public ExpressionFormatter getJavaSqlIndirectExpressionFormatter()
    {
        return javaSqlIndirectExpressionFormatter;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    private final JavaNativeMapper javaNativeMapper;
    private final String javaRootPackageName;

    private final ExpressionFormatter javaExpressionFormatter;
    private final ExpressionFormatter javaCaseExpressionFormatter;
    private final ExpressionFormatter javaSqlIndirectExpressionFormatter;

    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean withWriterCode;
}
