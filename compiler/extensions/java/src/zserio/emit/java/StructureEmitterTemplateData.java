package zserio.emit.java;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;

public final class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioEmitException
    {
        super(context, structureType);
    }
}
