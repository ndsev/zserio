package zserio.emit.java;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.PackageMapper;

final class TemplateDataContext
{
    public TemplateDataContext(JavaNativeTypeMapper javaNativeTypeMapper,
            PackageMapper javaPackageMapper, boolean withWriterCode, boolean withValidationCode,
            boolean withRangeCheckCode, String javaMajorVersion)
    {
        this.javaMajorVersion = javaMajorVersion;
        this.javaNativeTypeMapper = javaNativeTypeMapper;
        this.javaRootPackageName = javaPackageMapper.getRootPackageName();

        final JavaExpressionFormattingPolicy policy = new JavaExpressionFormattingPolicy(javaNativeTypeMapper);
        javaExpressionFormatter = new ExpressionFormatter(policy);

        final JavaCaseExpressionFormattingPolicy casePolicy =
                new JavaCaseExpressionFormattingPolicy(javaNativeTypeMapper);
        javaCaseExpressionFormatter = new ExpressionFormatter(casePolicy);

        final JavaSqlIndirectExpressionFormattingPolicy sqlIndirectPolicy =
                new JavaSqlIndirectExpressionFormattingPolicy(javaNativeTypeMapper);
        javaSqlIndirectExpressionFormatter = new ExpressionFormatter(sqlIndirectPolicy);

        this.withWriterCode = withWriterCode;
        this.withValidationCode = withValidationCode;
        this.withRangeCheckCode = withRangeCheckCode;
    }

    public String getJavaMajorVersion()
    {
        return javaMajorVersion;
    }

    public JavaNativeTypeMapper getJavaNativeTypeMapper()
    {
        return javaNativeTypeMapper;
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

    private final String                javaMajorVersion;
    private final JavaNativeTypeMapper  javaNativeTypeMapper;
    private final String                javaRootPackageName;

    private final ExpressionFormatter   javaExpressionFormatter;
    private final ExpressionFormatter   javaCaseExpressionFormatter;
    private final ExpressionFormatter   javaSqlIndirectExpressionFormatter;

    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean withWriterCode;
}
