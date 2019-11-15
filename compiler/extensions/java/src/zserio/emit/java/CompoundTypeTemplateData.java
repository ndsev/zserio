package zserio.emit.java;

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

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        final boolean withWriterCode = context.getWithWriterCode();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        compoundConstructorsData = new CompoundConstructorTemplateData(javaNativeMapper, withRangeCheckCode,
                withWriterCode, compoundType, javaExpressionFormatter);
        compoundParametersData = new CompoundParameterTemplateData(javaNativeMapper, withRangeCheckCode,
                withWriterCode, compoundType, javaExpressionFormatter);
        compoundFunctionsData = new CompoundFunctionTemplateData(javaNativeMapper, compoundType,
                javaExpressionFormatter);

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            fieldList.add(new CompoundFieldTemplateData(javaNativeMapper, withWriterCode,
                    withRangeCheckCode, compoundType, fieldType, javaExpressionFormatter));
        }
    }

    public CompoundConstructorTemplateData getCompoundConstructorsData()
    {
        return compoundConstructorsData;
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

    private final CompoundConstructorTemplateData   compoundConstructorsData;
    private final CompoundParameterTemplateData     compoundParametersData;
    private final CompoundFunctionTemplateData      compoundFunctionsData;
    private final boolean                           hasFieldWithOffset;
    private final List<CompoundFieldTemplateData>   fieldList;
}
