package zserio.extension.python;

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

        importPackage("typing");
        importPackage("zserio");

        withRangeCheckCode = context.getWithRangeCheckCode();
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        compoundParametersData = new CompoundParameterTemplateData(compoundType, pythonNativeMapper, this);
        compoundFunctionsData = new CompoundFunctionTemplateData(compoundType, pythonNativeMapper,
                pythonExpressionFormatter, this);

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
            fieldList.add(new CompoundFieldTemplateData(pythonNativeMapper, withRangeCheckCode,
                    compoundType, fieldType, pythonExpressionFormatter, this));
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

    private final boolean withRangeCheckCode;
    private final CompoundParameterTemplateData compoundParametersData;
    private final CompoundFunctionTemplateData compoundFunctionsData;
    private final boolean hasFieldWithOffset;
    private final List<CompoundFieldTemplateData> fieldList;
}
