package zserio.emit.cpp98;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.ArrayType;
import zserio.ast.Function;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.TypeReference;

public class InspectorZserioTypeNamesTemplateData extends CppTemplateData
{
    public InspectorZserioTypeNamesTemplateData(TemplateDataContext context, List<Field> fields,
            List<Function> functions, List<EnumType> enumTypes)
    {
        super(context);

        zserioTypeNames = new TreeSet<String>();
        for (Field field : fields)
        {
            final TypeReference fieldTypeReference = field.getTypeInstantiation().getTypeReference();
            final ZserioType fieldType = fieldTypeReference.getType();
            final String zserioTypeName = ZserioTypeUtil.getFullName(fieldType);
            zserioTypeNames.add(zserioTypeName);

            // add element type names for arrays as well
            final ZserioType fieldBaseType = fieldTypeReference.getBaseTypeReference().getType();
            if (fieldBaseType instanceof ArrayType)
            {
                final ArrayType arrayType = (ArrayType)fieldBaseType;
                final ZserioType elementType =
                        arrayType.getElementTypeInstantiation().getTypeReference().getType();
                final String elementZserioTypeName = ZserioTypeUtil.getFullName(elementType);
                zserioTypeNames.add(elementZserioTypeName);
            }
        }

        for (Function function : functions)
        {
            final ZserioType returnZserioType = function.getReturnTypeReference().getType();
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
