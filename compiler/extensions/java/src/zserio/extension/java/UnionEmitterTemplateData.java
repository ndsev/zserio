package zserio.extension.java;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;

public final class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType) throws ZserioExtensionException
    {
        super(context, unionType);
    }
}
