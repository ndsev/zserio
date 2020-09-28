package zserio.emit.doc;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;

public class SubtypeTemplateData extends DocTemplateData
{
    public SubtypeTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioEmitException
    {
        super(context, subtype, subtype.getName());

        linkedType = new LinkedType(subtype.getTypeReference());
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    private final LinkedType linkedType;
};
