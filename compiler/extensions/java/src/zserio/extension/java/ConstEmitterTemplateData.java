package zserio.extension.java;

import zserio.ast.Constant;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.symbols.JavaNativeSymbol;
import zserio.extension.java.types.JavaNativeType;

public final class ConstEmitterTemplateData extends JavaTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant) throws ZserioExtensionException
    {
        super(context);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();

        final JavaNativeSymbol constantNativeSymbol = javaNativeMapper.getJavaSymbol(constant);
        packageName = JavaFullNameFormatter.getFullName(constantNativeSymbol.getPackageName());
        name = constantNativeSymbol.getName();

        final JavaNativeType nativeTargetType = javaNativeMapper.getJavaType(constant.getTypeInstantiation());
        javaTypeName = nativeTargetType.getFullName();

        value = javaExpressionFormatter.formatGetter(constant.getValueExpression());
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public String getJavaTypeName()
    {
        return javaTypeName;
    }

    public String getValue()
    {
        return value;
    }

    private final String packageName;
    private final String name;
    private final String javaTypeName;
    private final String value;
}
