package zserio.extension.java;

import zserio.ast.CompoundType;
import zserio.extension.common.ZserioExtensionException;

public final class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(CompoundType compoundType,
            CompoundParameterTemplateData compoundParametersData) throws ZserioExtensionException
    {
        compoundName = compoundType.getName();
        this.compoundParametersData = compoundParametersData;
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    private final String                        compoundName;
    private final CompoundParameterTemplateData compoundParametersData;
}
