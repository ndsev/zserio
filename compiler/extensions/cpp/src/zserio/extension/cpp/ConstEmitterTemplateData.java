package zserio.extension.cpp;

import java.util.List;

import zserio.ast.Constant;
import zserio.ast.DocComment;
import zserio.ast.StringType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeStringViewType;

/**
 * FreeMarker template data for ConstEmitter.
 */
public class ConstEmitterTemplateData extends CppTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioExtensionException
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
            typeInfo = new NativeTypeInfoTemplateData(nativeStringViewType, constantTypeInstantation);
        }
        else
        {
            final CppNativeType nativeTargetType = cppNativeMapper.getCppType(constantTypeInstantation);
            addHeaderIncludesForType(nativeTargetType);
            typeInfo = new NativeTypeInfoTemplateData(nativeTargetType, constantTypeInstantation);
        }

        value = cppExpressionFormatter.formatGetter(constant.getValueExpression());
        final List<DocComment> itemDocComments = constant.getDocComments();
        docComments = itemDocComments.isEmpty()
                ? null : new DocCommentsTemplateData(context, itemDocComments);
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
    }

    public String getName()
    {
        return name;
    }

    public NativeTypeInfoTemplateData getTypeInfo()
    {
        return typeInfo;
    }

    public String getValue()
    {
        return value;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    private final PackageTemplateData packageData;
    private final String name;
    private final NativeTypeInfoTemplateData typeInfo;
    private final String value;
    private final DocCommentsTemplateData docComments;
}
