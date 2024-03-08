package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for compound types.
 */
public class CompoundTypeTemplateData extends UserTypeTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        super(context, compoundType, compoundType);

        usedInPackedArray = context.getPackedTypesCollector().isUsedInPackedArray(compoundType);
        isPackable = compoundType.isPackable();

        compoundParametersData = new CompoundParameterTemplateData(context, compoundType);
        compoundFunctionsData = new CompoundFunctionTemplateData(context, compoundType);

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
            fieldList.add(new CompoundFieldTemplateData(context, compoundType, fieldType));

        templateInstantiation = TemplateInstantiationTemplateData.create(context, compoundType);
    }

    public boolean getUsedInPackedArray()
    {
        return usedInPackedArray;
    }

    public boolean getIsPackable()
    {
        return isPackable;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    public CompoundFunctionTemplateData getCompoundFunctionsData()
    {
        return compoundFunctionsData;
    }

    public Iterable<CompoundFieldTemplateData> getFieldList()
    {
        return fieldList;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    private final boolean usedInPackedArray;
    private final boolean isPackable;
    private final CompoundParameterTemplateData compoundParametersData;
    private final CompoundFunctionTemplateData compoundFunctionsData;
    private final List<CompoundFieldTemplateData> fieldList;
    private final TemplateInstantiationTemplateData templateInstantiation;
}
