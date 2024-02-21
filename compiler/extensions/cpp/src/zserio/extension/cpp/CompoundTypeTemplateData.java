package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.ast.ZserioTypeUtil;
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

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            final CompoundFieldTemplateData data =
                    new CompoundFieldTemplateData(context, compoundType, fieldType, this);

            fieldList.add(data);
        }

        compoundParametersData = new CompoundParameterTemplateData(context, compoundType, this);
        for (CompoundParameterTemplateData.CompoundParameter parameter : compoundParametersData.getList())
        {
            System.out.println("INFO: param '" + ZserioTypeUtil.getFullName(compoundType)
                    + "." + parameter.getName() + "' is used "
                    + (parameter.getUsesSharedPointer() ? "as shared pointer" : "by value")
                    + (parameter.getTypeInfo().getIsCompound() ? " (compound)" : " (non-compound)")
                    + ": " + parameter.getNumBytesTreshold() + "B");
        }

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

    private final boolean usedInPackedArray;

    private final List<CompoundFieldTemplateData> fieldList;
    private final CompoundParameterTemplateData compoundParametersData;
    private final CompoundFunctionTemplateData compoundFunctionsData;
    private final CompoundConstructorTemplateData compoundConstructorsData;

    private final boolean isPackable;
    private final boolean needsChildrenInitialization;

    private final TemplateInstantiationTemplateData templateInstantiation;
}
