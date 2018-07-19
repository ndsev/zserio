package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ConstType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeConstType;

public class ConstEmitterTemplateData extends CppTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context)
    {
        super(context);
        cppNativeTypeMapper = context.getCppNativeTypeMapper();
        cppExpressionFormatter = context.getExpressionFormatter(this);
        items = new ArrayList<Item>();
    }

    public void add(ConstType constType)
    {
        final CppNativeType nativeType = cppNativeTypeMapper.getCppType(constType);
        if (!(nativeType instanceof NativeConstType))
            throw new InternalError("A const type mapped to something else than NativeConstType!");

        final NativeConstType nativeConstType = (NativeConstType)nativeType;
        items.add(new Item(constType, nativeConstType, cppExpressionFormatter));

        /*
         * don't use nativeConstType here to avoid adding "ConstType.h" into the list here,
         * add the target type instead
         */
        addHeaderIncludesForType(nativeConstType.getTargetType());
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public Iterable<Item> getItems()
    {
        return items;
    }

    public static class Item
    {
        public Item(ConstType constType, NativeConstType nativeConstType,
                ExpressionFormatter cppExpressionFormatter)
        {
            name = nativeConstType.getName();

            final CppNativeType nativeTargetType = nativeConstType.getTargetType();
            cppTypeName = nativeTargetType.getFullName();
            final Expression valueExpression = constType.getValueExpression();
            value = cppExpressionFormatter.formatGetter(valueExpression);
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

        private final String name;
        private final String cppTypeName;
        private final String value;
    }

    private final CppNativeTypeMapper   cppNativeTypeMapper;
    private final ExpressionFormatter   cppExpressionFormatter;
    private final List<Item>            items;
}
