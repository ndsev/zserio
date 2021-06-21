package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base class for compound types template data for FreeMarker.
 */
public class CompoundTypeTemplateData extends UserTypeTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        super(context, compoundType);

        importPackage("typing");
        importPackage("zserio");

        withRangeCheckCode = context.getWithRangeCheckCode();
        compoundParametersData = new CompoundParameterTemplateData(context, compoundType, this);
        compoundFunctionsData = new CompoundFunctionTemplateData(context, compoundType, this);

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            fieldList.add(new CompoundFieldTemplateData(context, compoundType, fieldType, this));
        }

        templateInstantiation = TemplateInstantiationTemplateData.create(context, compoundType, this);
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    public CompoundFunctionTemplateData getCompoundFunctionsData()
    {
        return compoundFunctionsData;
    }

    public boolean getHasFieldWithOffset()
    {
        return hasFieldWithOffset;
    }

    public Iterable<CompoundFieldTemplateData> getFieldList()
    {
        return fieldList;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    private final boolean withRangeCheckCode;
    private final CompoundParameterTemplateData compoundParametersData;
    private final CompoundFunctionTemplateData compoundFunctionsData;
    private final boolean hasFieldWithOffset;
    private final List<CompoundFieldTemplateData> fieldList;
    private final TemplateInstantiationTemplateData templateInstantiation;
}
