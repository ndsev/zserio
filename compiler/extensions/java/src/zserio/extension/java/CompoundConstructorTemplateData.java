package zserio.extension.java;

import zserio.ast.CompoundType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public final class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(JavaNativeMapper javaNativeMapper, boolean withRangeCheckCode,
            boolean withWriterCode, CompoundType compoundType, ExpressionFormatter javaExpressionFormatter)
                    throws ZserioExtensionException
    {
        compoundName = compoundType.getName();
        this.withWriterCode = withWriterCode;
        compoundParametersData = new CompoundParameterTemplateData(javaNativeMapper, withRangeCheckCode,
                withWriterCode, compoundType, javaExpressionFormatter);
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    private final String                        compoundName;
    private final boolean                       withWriterCode;
    private final CompoundParameterTemplateData compoundParametersData;
}
