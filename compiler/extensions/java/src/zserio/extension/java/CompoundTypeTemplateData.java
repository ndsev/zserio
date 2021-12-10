package zserio.extension.java;

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

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();

        compoundParametersData = new CompoundParameterTemplateData(javaNativeMapper, withRangeCheckCode,
                compoundType, javaExpressionFormatter);
        compoundConstructorsData = new CompoundConstructorTemplateData(compoundType, compoundParametersData);
        compoundFunctionsData = new CompoundFunctionTemplateData(javaNativeMapper, compoundType,
                javaExpressionFormatter);

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
        {
            fieldList.add(new CompoundFieldTemplateData(javaNativeMapper, withRangeCheckCode, compoundType,
                    fieldType, javaExpressionFormatter));
        }
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    public CompoundConstructorTemplateData getCompoundConstructorsData()
    {
        return compoundConstructorsData;
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

    private final CompoundParameterTemplateData     compoundParametersData;
    private final CompoundConstructorTemplateData   compoundConstructorsData;
    private final CompoundFunctionTemplateData      compoundFunctionsData;
    private final boolean                           hasFieldWithOffset;
    private final List<CompoundFieldTemplateData>   fieldList;
}
