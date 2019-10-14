package zserio.emit.cpp98;

import zserio.ast.ConstType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.CppNativeType;

public class ConstEmitterTemplateData extends UserTypeTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, ConstType constType) throws ZserioEmitException
    {
        super(context, constType);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(
                new HeaderIncludeCollectorAdapter(this));

        name = constType.getName();
        CppNativeType nativeTargetType = cppNativeTypeMapper.getCppType(constType.getTypeReference());
        cppTypeName = nativeTargetType.getFullName();
        value = cppExpressionFormatter.formatGetter(constType.getValueExpression());

        addHeaderIncludesForType(nativeTargetType);
    }

    public String getName()
    {
        return name;
    }

    public String getCppTypeName()
    {
        return cppTypeName;
    }

    public String getValue()
    {
        return value;
    }

    private final String cppTypeName;
    private final String name;
    private final String value;
}
