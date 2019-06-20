package zserio.emit.cpp;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.ArrayType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.FunctionType;
import zserio.ast.TypeReference;

public class InspectorZserioTypeNamesTemplateData extends CppTemplateData
{
    public InspectorZserioTypeNamesTemplateData(TemplateDataContext context, List<Field> fields,
            List<FunctionType> functionTypes, List<EnumType> enumTypes)
    {
        super(context);

        zserioTypeNames = new TreeSet<String>();
        for (Field field : fields)
        {
            final ZserioType fieldType = TypeReference.resolveType(field.getFieldType());
            final String zserioTypeName = ZserioTypeUtil.getFullName(fieldType);
            zserioTypeNames.add(zserioTypeName);

            // add element type names for arrays as well
            final ZserioType baseFieldType = TypeReference.resolveBaseType(fieldType);
            if (baseFieldType instanceof ArrayType)
            {
                final ArrayType arrayType = (ArrayType)baseFieldType;
                final ZserioType elementType = TypeReference.resolveBaseType(arrayType.getElementType());
                final String elementZserioTypeName = ZserioTypeUtil.getFullName(elementType);
                zserioTypeNames.add(elementZserioTypeName);
            }
        }

        for (FunctionType functionType : functionTypes)
        {
            final ZserioType returnZserioType = functionType.getReturnType();
            final String zserioReturnTypeName = ZserioTypeUtil.getFullName(returnZserioType);
            zserioTypeNames.add(zserioReturnTypeName);
        }

        for (EnumType enumType : enumTypes)
        {
            // This is needed because all enumerations have theirs own 'tree' write method which needs Zserio
            // type name regardless if the enumeration is used by compound field or not. The 'tree' write method
            // for enumeration types is necessary for enumeration arrays.
            final String zserioEnumTypeName = ZserioTypeUtil.getFullName(enumType);
            zserioTypeNames.add(zserioEnumTypeName);
        }
    }

    public Iterable<String> getZserioTypeNames()
    {
        return zserioTypeNames;
    }

    private final Set<String> zserioTypeNames;
}
