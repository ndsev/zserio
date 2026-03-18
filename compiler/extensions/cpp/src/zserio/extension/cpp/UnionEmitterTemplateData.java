package zserio.extension.cpp;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for UnionEmitter.
 */
public final class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType)
            throws ZserioExtensionException
    {
        super(context, unionType);
    }

    public static UnionEmitterTemplateData create(TemplateDataContext context, UnionType unionType)
            throws ZserioExtensionException
    {
        final UnionEmitterTemplateData self = new UnionEmitterTemplateData(context, unionType);
        self.init(context, unionType);
        return self;
    }
}
