package zserio.extension.cpp;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for StructureEmitter.
 */
public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioExtensionException
    {
        super(context, structureType);
    }
}
