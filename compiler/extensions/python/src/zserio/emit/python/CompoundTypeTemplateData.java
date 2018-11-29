package zserio.emit.python;

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

        withRangeCheckCode = context.getWithRangeCheckCode();
        final PythonNativeTypeMapper javaNativeTypeMapper = context.getPythonNativeTypeMapper();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        compoundParametersData = new CompoundParameterTemplateData(compoundType);
        compoundFunctionsData = new CompoundFunctionTemplateData(compoundType, pythonExpressionFormatter);

        hasFieldWithOffset = compoundType.hasFieldWithOffset();

        final List<Field> fieldTypeList = compoundType.getFields();
        fieldList = new ArrayList<CompoundFieldTemplateData>(fieldTypeList.size());
        for (Field fieldType : fieldTypeList)
            fieldList.add(new CompoundFieldTemplateData(javaNativeTypeMapper, withRangeCheckCode, fieldType,
                    pythonExpressionFormatter));

        importRuntimePackage();
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
