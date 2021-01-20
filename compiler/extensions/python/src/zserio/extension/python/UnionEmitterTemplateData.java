package zserio.extension.python;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;

public class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType)
            throws ZserioExtensionException
    {
        super(context, unionType);
    }

    public String getUndefinedChoiceTagName()
    {
        return UNDEFINED_CHOICE_TAG_NAME;
    }

    public String getChoiceTagName(String fieldName)
    {
        return CHOICE_TAG_NAME_PREFIX + fieldName;
    }

    private final static String UNDEFINED_CHOICE_TAG_NAME= "UNDEFINED_CHOICE";
    private final static String CHOICE_TAG_NAME_PREFIX = "CHOICE_";
}
