package zserio.extension.java;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for StructureEmitter.
 */
public final class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioExtensionException
    {
        super(context, structureType);
    }
}
