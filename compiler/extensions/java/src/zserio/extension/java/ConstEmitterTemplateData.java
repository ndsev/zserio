package zserio.extension.java;

import java.util.List;

import zserio.ast.Constant;
import zserio.ast.DocComment;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.symbols.JavaNativeSymbol;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for ConstEmitter.
 */
public final class ConstEmitterTemplateData extends JavaTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioExtensionException
    {
        super(context);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();

        final JavaNativeSymbol constantNativeSymbol = javaNativeMapper.getJavaSymbol(constant);
        packageName = JavaFullNameFormatter.getFullName(constantNativeSymbol.getPackageName());
        name = constantNativeSymbol.getName();

        final TypeInstantiation typeInstantiation = constant.getTypeInstantiation();
        final JavaNativeType nativeTargetType = javaNativeMapper.getJavaType(typeInstantiation);
        typeInfo = new NativeTypeInfoTemplateData(nativeTargetType, typeInstantiation);

        value = javaExpressionFormatter.formatGetter(constant.getValueExpression());

        final List<DocComment> itemDocComments = constant.getDocComments();
        docComments = itemDocComments.isEmpty() ? null : new DocCommentsTemplateData(context, itemDocComments);
    }

    public String getPackageName()
    {
        return packageName;
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

    private final String packageName;
    private final String name;
    private final NativeTypeInfoTemplateData typeInfo;
    private final String value;
    private final DocCommentsTemplateData docComments;
}
