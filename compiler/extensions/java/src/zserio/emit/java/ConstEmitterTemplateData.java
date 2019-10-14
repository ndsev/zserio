package zserio.emit.java;

import zserio.ast.ConstType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;

public final class ConstEmitterTemplateData extends UserTypeTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, ConstType constType) throws ZserioEmitException
    {
        super(context, constType);

        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();

        final JavaNativeType nativeTargetType = javaNativeTypeMapper.getJavaType(constType.getTypeReference());
        javaTypeName = nativeTargetType.getFullName();

        value = javaExpressionFormatter.formatGetter(constType.getValueExpression());
    }

    public String getJavaTypeName()
    {
        return javaTypeName;
    }

    public String getValue()
    {
        return value;
    }

    private final String javaTypeName;
    private final String value;
}
