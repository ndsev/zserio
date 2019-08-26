package zserio.emit.cpp;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;

public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType,
            boolean usedInRpc) throws ZserioEmitException
    {
        super(context, structureType);
        needsRpcTraits = usedInRpc;
    }

    public boolean getNeedsRpcTraits()
    {
        return needsRpcTraits;
    }

    private final boolean needsRpcTraits;
}
