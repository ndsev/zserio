package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Constant;
import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;

public class SubtypeTemplateData extends DocTemplateData
{
    public SubtypeTemplateData(TemplateDataContext context, Subtype subtype) throws ZserioEmitException
    {
        super(context, subtype, subtype.getName());

        linkedType = new LinkedType(subtype.getTypeReference());

        for (Constant constant : context.getUsedByCollector().getUsedByTypes(subtype, Constant.class))
        {
            constInstances.add(new LinkedType(constant));
        }
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    public Iterable<LinkedType> getConstInstances()
    {
        return constInstances;
    }

    private final LinkedType linkedType;
    private final List<LinkedType> constInstances = new ArrayList<LinkedType>();
};
