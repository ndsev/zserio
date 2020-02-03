package zserio.emit.cpp;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;

public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioEmitException
    {
        super(context, structureType);
    }
}
