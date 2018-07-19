package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ConstType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeConstType;

public final class ConstEmitterTemplateData extends JavaTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context)
    {
        super(context);

        this.javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        packageName = context.getJavaRootPackageName();
        this.javaExpressionFormatter = context.getJavaExpressionFormatter();
        name = NativeConstType.getClassName();
        items = new ArrayList<ConstItemData>();
    }

    public void add(ConstType constType) throws ZserioEmitJavaException
    {
        final JavaNativeType nativeType = javaNativeTypeMapper.getJavaType(constType);
        if (!(nativeType instanceof NativeConstType))
            throw new InternalError("A const type mapped to something else than NativeConstType!");

        final NativeConstType nativeConstType = (NativeConstType)nativeType;
        items.add(new ConstItemData(constType, nativeConstType, packageName, javaExpressionFormatter));
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public Iterable<ConstItemData> getItems()
    {
        return items;
    }

    public static class ConstItemData
    {
        public ConstItemData(ConstType constType, NativeConstType nativeConstType, String packageName,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitJavaException
        {
            name = constType.getName();
            if (!packageName.equals(nativeConstType.getPackageName()))
                throw new ZserioEmitJavaException(constType);

            // subtype is already resolved by the native type mapper - there are not native subtypes in java
            final JavaNativeType nativeTargetType = nativeConstType.getTargetType();
            javaTypeName = nativeTargetType.getFullName();
            final Expression valueExpression = constType.getValueExpression();
            value = javaExpressionFormatter.formatGetter(valueExpression);
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

        private final String name;
        private final String javaTypeName;
        private final String value;
    }

    private final JavaNativeTypeMapper  javaNativeTypeMapper;
    private final String                packageName;
    private final ExpressionFormatter   javaExpressionFormatter;
    private final String                name;
    private final List<ConstItemData>   items;
}
