package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.ast.ZserioType;

public class ApiEmitterTemplateData extends PythonTemplateData
{
    public ApiEmitterTemplateData(PackageName packageName)
    {
        this.packageName = packageName;
    }

    public PackageName getPackageName()
    {
        return packageName;
    }

    public String getPackagePath()
    {
        return PythonFullNameFormatter.getFullName(packageName);
    }

    public Iterable<String> getTypes()
    {
        return types;
    }

    public Iterable<String> getSubpackages()
    {
        return subpackages;
    }

    public void addType(ZserioType zserioType)
    {
        types.add(zserioType.getName());
    }

    public void addSubpackage(String subpackage)
    {
        subpackages.add(subpackage);
    }

    final PackageName packageName;
    final List<String> types = new ArrayList<String>();
    final TreeSet<String> subpackages = new TreeSet<String>();
}
