package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.CompoundParameterTemplateData.CompoundParameter;

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

    // formats parameters as function arguments
    // withTypes: uint16_t a_, Param& b_
    // !withTypes: a_, b_
    public String parameterArgs(boolean withTypes)
    {
        ArrayList<String> args = new ArrayList<String>();
        for (CompoundParameter param : compoundParametersData.getList())
        {
            String s = "";
            if (withTypes)
            {
                s += param.getTypeInfo().getTypeFullName();
                if (!param.getTypeInfo().getIsSimple())
                    s += "&";
                s += " ";
            }
            s += param.getCppArgName();
            args.add(s);
        }
        return String.join(", ", args);
    }

    // formats NoInit constructor initializers - only struct and array fields
    public ArrayList<String> noInitInitializers()
    {
        ArrayList<String> inits = new ArrayList<String>();
        for (CompoundFieldTemplateData field : fieldList)
        {
            if (field.getTypeInfo().getIsSimple())
                continue;
            if ((field.getOptional() == null && !field.getNeedsAllocator()) ||
                    (field.getOptional() != null && !field.getHolderNeedsAllocator()))
                continue;

            String s = field.getCppName() + "(";

            if (field.getCompound() != null && field.getOptional() == null && field.getArray() == null)
                s += "::zserio::NoInit, ";

            s += "allocator)";

            inits.add(s);
        }
        return inits;
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
