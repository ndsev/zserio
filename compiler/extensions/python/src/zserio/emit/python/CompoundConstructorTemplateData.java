package zserio.emit.python;

import zserio.ast.CompoundType;
import zserio.emit.common.ZserioEmitException;

public final class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(boolean withWriterCode, CompoundType compoundType)
            throws ZserioEmitException
    {
        this.withWriterCode = withWriterCode;
        compoundParametersData = new CompoundParameterTemplateData(compoundType);
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    private final boolean withWriterCode;
    private final CompoundParameterTemplateData compoundParametersData;
}
