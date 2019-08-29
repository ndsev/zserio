package zserio.emit.cpp;

import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType,
            boolean usedInRpc) throws ZserioEmitException
    {
        super(context, unionType);
        needsRpcTraits = usedInRpc;
    }

    public boolean getNeedsRpcTraits()
    {
        return needsRpcTraits;
    }

    private final boolean needsRpcTraits;
}
