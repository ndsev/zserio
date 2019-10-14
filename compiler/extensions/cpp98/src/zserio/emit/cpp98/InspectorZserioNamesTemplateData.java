package zserio.emit.cpp98;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Function;

public class InspectorZserioNamesTemplateData extends CppTemplateData
{
    public InspectorZserioNamesTemplateData(TemplateDataContext context, List<Field> fields,
            List<Function> functions, List<EnumType> enumTypes)
    {
        super(context);

        zserioNames = new TreeSet<String>();
        for (Field field : fields)
            zserioNames.add(field.getName());

        for (Function functionType : functions)
            zserioNames.add(functionType.getName());

        // This is needed because all enumerations have theirs own 'tree' write method which needs Zserio
        // name regardless if the enumeration is used by compound field or not. The 'tree' write method
        // for enumeration types is necessary for enumeration arrays.
        for (EnumType enumType : enumTypes)
            zserioNames.add(enumType.getName());
    }

    public Iterable<String> getZserioNames()
    {
        return zserioNames;
    }

    private final Set<String> zserioNames;
}
