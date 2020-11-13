package zserio.extension.cpp;

import zserio.ast.Constant;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;

public class ConstEmitterTemplateData extends CppTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant) throws ZserioExtensionException
    {
        super(context);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(
                new HeaderIncludeCollectorAdapter(this));

        final CppNativeSymbol constantNativeSymbol = cppNativeMapper.getCppSymbol(constant);
        packageData = new PackageTemplateData(constantNativeSymbol.getPackageName());
        name = constantNativeSymbol.getName();

        CppNativeType nativeTargetType = cppNativeMapper.getCppType(constant.getTypeInstantiation());
        addHeaderIncludesForType(nativeTargetType);

        cppTypeName = nativeTargetType.getFullName();
        value = cppExpressionFormatter.formatGetter(constant.getValueExpression());
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
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

    private final PackageTemplateData packageData;
    private final String name;
    private final String cppTypeName;
    private final String value;
}
