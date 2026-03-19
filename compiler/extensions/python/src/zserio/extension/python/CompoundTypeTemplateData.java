package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base class for compound types template data for FreeMarker.
 */
public abstract class CompoundTypeTemplateData extends UserTypeTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        super(context, compoundType, compoundType);
    }

    public void init(TemplateDataContext context, CompoundType compoundType) throws ZserioExtensionException
    {
        importPackage("typing");
        importPackage("zserio");

        usedInPackedArray = context.getPackedTypesCollector().isUsedInPackedArray(compoundType);

        withRangeCheckCode = context.getWithRangeCheckCode();
        compoundParametersData = new CompoundParameterTemplateData(context, compoundType, this);
        compoundFunctionsData = new CompoundFunctionTemplateData(context, compoundType, this);

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            fieldList.add(new CompoundFieldTemplateData(context, compoundType, fieldType, this));
        }

        isPackable = compoundType.isPackable();

        templateInstantiation = TemplateInstantiationTemplateData.create(context, compoundType, this);
    }

    public boolean getUsedInPackedArray()
    {
        return usedInPackedArray;
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

    public Iterable<CompoundFieldTemplateData> getFieldList()
    {
        return fieldList;
    }

    public boolean getIsPackable()
    {
        return isPackable;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    private boolean usedInPackedArray;
    private boolean withRangeCheckCode;
    private CompoundParameterTemplateData compoundParametersData;
    private CompoundFunctionTemplateData compoundFunctionsData;
    private List<CompoundFieldTemplateData> fieldList;
    private boolean isPackable;
    private TemplateInstantiationTemplateData templateInstantiation;
}
