package zserio.emit.java;

import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public final class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType) throws ZserioEmitException
    {
        super(context, unionType);
    }
}
