package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.NativeIntegralType;

public class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(bitmaskTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        baseCppTypeName = nativeBaseType.getFullName();

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(
                bitmaskTypeInstantiation, cppExpressionFormatter);

        final List<BitmaskValue> bitmaskValues = bitmaskType.getValues();
        values = new ArrayList<BitmaskValueData>(bitmaskValues.size());
        for (BitmaskValue bitmaskValue : bitmaskValues)
            values.add(new BitmaskValueData(nativeBaseType, bitmaskValue));
    }

    public String getBaseCppTypeName()
    {
        return baseCppTypeName;
    }

    public RuntimeFunctionTemplateData getRuntimeFunction()
    {
        return runtimeFunction;
    }

    public Iterable<BitmaskValueData> getValues()
    {
        return values;
    }

    public static class BitmaskValueData
    {
        public BitmaskValueData(NativeIntegralType nativeBaseType, BitmaskValue bitmaskValue)
                throws ZserioEmitException
        {
            name = bitmaskValue.getName();
            value = nativeBaseType.formatLiteral(bitmaskValue.getValue());
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        private final String name;
        private final String value;
    };

    private final String baseCppTypeName;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<BitmaskValueData> values;
}
