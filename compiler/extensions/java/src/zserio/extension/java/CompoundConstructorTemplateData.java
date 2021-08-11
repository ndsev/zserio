package zserio.extension.java;

import zserio.ast.CompoundType;
import zserio.extension.common.ZserioExtensionException;

public final class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(boolean withWriterCode, CompoundType compoundType,
            CompoundParameterTemplateData compoundParametersData) throws ZserioExtensionException
    {
        compoundName = compoundType.getName();
        this.withWriterCode = withWriterCode;
        this.compoundParametersData = compoundParametersData;
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
