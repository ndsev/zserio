package zserio.extension.cpp;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;

public class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType) throws ZserioExtensionException
    {
        super(context, unionType);
    }
}
