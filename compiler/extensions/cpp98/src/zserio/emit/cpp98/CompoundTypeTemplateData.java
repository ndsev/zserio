package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class CompoundTypeTemplateData extends UserTypeTemplateData
{
    public CompoundTypeTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioEmitException
    {
        super(context, compoundType);

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        final boolean withWriterCode = context.getWithWriterCode();
        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        final ExpressionFormatter cppIndirectExpressionFormatter =
                context.getOwnerIndirectExpressionFormatter(this);
        for (Field fieldType : fieldTypeList)
        {
            final CompoundFieldTemplateData data = new CompoundFieldTemplateData(cppNativeTypeMapper,
                    compoundType, fieldType, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    this, withWriterCode);

            fieldList.add(data);
        }

        compoundParametersData = new CompoundParameterTemplateData(cppNativeTypeMapper, compoundType, this,
                withWriterCode);
        compoundFunctionsData = new CompoundFunctionTemplateData(cppNativeTypeMapper, compoundType,
                cppExpressionFormatter, this);
        compoundConstructorsData = new CompoundConstructorTemplateData(cppNativeTypeMapper, compoundType,
                compoundParametersData, fieldList);

        needsChildrenInitialization = compoundType.needsChildrenInitialization();
        withRangeCheckCode = context.getWithRangeCheckCode();
        hasFieldWithOffset = compoundType.hasFieldWithOffset();
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

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getHasFieldWithOffset()
    {
        return hasFieldWithOffset;
    }

    private final List<CompoundFieldTemplateData>   fieldList;
    private final CompoundParameterTemplateData     compoundParametersData;
    private final CompoundFunctionTemplateData      compoundFunctionsData;
    private final CompoundConstructorTemplateData   compoundConstructorsData;

    private final boolean needsChildrenInitialization;
    private final boolean withRangeCheckCode;
    private final boolean hasFieldWithOffset;
}
