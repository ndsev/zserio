package zserio.emit.cpp98;

import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType) throws ZserioEmitException
    {
        super(context, unionType);
    }
}
