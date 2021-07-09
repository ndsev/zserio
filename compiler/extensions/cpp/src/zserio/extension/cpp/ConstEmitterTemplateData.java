package zserio.extension.cpp;

import zserio.ast.Constant;
import zserio.ast.StringType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeStringViewType;

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

        final TypeInstantiation constantTypeInstantation = constant.getTypeInstantiation();
        if (constantTypeInstantation.getBaseType() instanceof StringType)
        {
            final NativeStringViewType nativeStringViewType = cppNativeMapper.getStringViewType();
            addHeaderIncludesForType(nativeStringViewType);
            cppTypeName = nativeStringViewType.getFullName();

            final String stringValue = constant.getValueExpression().getStringValue();
            if (stringValue == null)
                throw new ZserioExtensionException("Unexpected value expression which is a non-constant string!");
            value = nativeStringViewType.formatLiteral(stringValue);
        }
        else
        {
            final CppNativeType nativeTargetType = cppNativeMapper.getCppType(constantTypeInstantation);
            addHeaderIncludesForType(nativeTargetType);
            cppTypeName = nativeTargetType.getFullName();
            value = cppExpressionFormatter.formatGetter(constant.getValueExpression());
        }
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
