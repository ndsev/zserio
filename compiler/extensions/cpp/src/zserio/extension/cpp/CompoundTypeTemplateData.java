package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for compound types.
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
        usedInPackedArray = context.getPackedTypesCollector().isUsedInPackedArray(compoundType);

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            final CompoundFieldTemplateData data =
                    new CompoundFieldTemplateData(context, compoundType, fieldType, this);

            fieldList.add(data);
        }

        compoundParametersData = new CompoundParameterTemplateData(context, compoundType, this);
        compoundFunctionsData = new CompoundFunctionTemplateData(context, compoundType, this);
        compoundConstructorsData =
                new CompoundConstructorTemplateData(compoundType, compoundParametersData, fieldList);

        isPackable = compoundType.isPackable();

        // TODO[Mi-L@] Similar logic is done in freemarker template function (has_field_with_initialization).
        //             Try to unify the logic!
        needsChildrenInitialization = compoundType.needsChildrenInitialization();

        templateInstantiation = TemplateInstantiationTemplateData.create(context, compoundType, this);
    }

    public boolean getUsedInPackedArray()
    {
        return usedInPackedArray;
    }

    public Iterable<CompoundFieldTemplateData> getFieldList()
    {
        return fieldList;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    public CompoundFunctionTemplateData getCompoundFunctionsData()
    {
        return compoundFunctionsData;
    }

    public CompoundConstructorTemplateData getCompoundConstructorsData()
    {
        return compoundConstructorsData;
    }

    public boolean getIsPackable()
    {
        return isPackable;
    }

    public boolean getNeedsChildrenInitialization()
    {
        return needsChildrenInitialization;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    private boolean usedInPackedArray;

    private List<CompoundFieldTemplateData> fieldList;
    private CompoundParameterTemplateData compoundParametersData;
    private CompoundFunctionTemplateData compoundFunctionsData;
    private CompoundConstructorTemplateData compoundConstructorsData;

    private boolean isPackable;
    private boolean needsChildrenInitialization;

    private TemplateInstantiationTemplateData templateInstantiation;
}
