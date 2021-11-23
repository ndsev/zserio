package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public class CompoundTypeTemplateData extends UserTypeTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        super(context, compoundType);

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        final boolean withWriterCode = context.getWithWriterCode();
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        final ExpressionFormatter cppIndirectExpressionFormatter =
                context.getIndirectExpressionFormatter(this, "m_owner");
        for (Field fieldType : fieldTypeList)
        {
            final CompoundFieldTemplateData data = new CompoundFieldTemplateData(cppNativeMapper,
                    compoundType, fieldType, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    this, withWriterCode, withRangeCheckCode);

            fieldList.add(data);
        }

        compoundParametersData = new CompoundParameterTemplateData(cppNativeMapper, compoundType, this,
                withWriterCode);
        compoundFunctionsData = new CompoundFunctionTemplateData(cppNativeMapper, compoundType,
                cppExpressionFormatter, this);
        compoundConstructorsData = new CompoundConstructorTemplateData(cppNativeMapper, compoundType,
                compoundParametersData, fieldList);

        // TODO[Mi-L@] Similar logic is done in freemarker template function (has_field_with_initialization).
        //             Try to unify the logic!
        needsChildrenInitialization = compoundType.needsChildrenInitialization();

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        templateInstantiation = TemplateInstantiationTemplateData.create(context, compoundType, this);
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

    public boolean getNeedsChildrenInitialization()
    {
        return needsChildrenInitialization;
    }

    public boolean getHasFieldWithOffset()
    {
        return hasFieldWithOffset;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    private final List<CompoundFieldTemplateData> fieldList;
    private final CompoundParameterTemplateData compoundParametersData;
    private final CompoundFunctionTemplateData compoundFunctionsData;
    private final CompoundConstructorTemplateData compoundConstructorsData;
    private final TemplateInstantiationTemplateData templateInstantiation;

    private final boolean needsChildrenInitialization;
    private final boolean hasFieldWithOffset;
}
