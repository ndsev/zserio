package zserio.extension.python;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;

public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioExtensionException
    {
        super(context, structureType);
    }
}
