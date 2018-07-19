package zserio.emit.cpp;

import java.util.Set;
import java.util.TreeSet;

import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.FunctionType;

public class InspectorZserioNamesTemplateData extends CppTemplateData
{
    public InspectorZserioNamesTemplateData(TemplateDataContext context)
    {
        super(context);
        zserioNames = new TreeSet<String>();
    }

    public void add(Field field)
    {
        zserioNames.add(field.getName());
    }

    public void add(FunctionType functionType)
    {
        zserioNames.add(functionType.getName());
    }

    public void add(EnumType enumType)
    {
        // This is needed because all enumerations have theirs own 'tree' write method which needs Zserio
        // name regardless if the enumeration is used by compound field or not. The 'tree' write method
        // for enumeration types is necessary for enumeration arrays.
        zserioNames.add(enumType.getName());
    }

    public Iterable<String> getZserioNames()
    {
        return zserioNames;
    }

    private final Set<String> zserioNames;
}
