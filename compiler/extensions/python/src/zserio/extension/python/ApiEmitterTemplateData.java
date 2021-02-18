package zserio.extension.python;

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import zserio.ast.PackageName;
import zserio.ast.ZserioType;

public class ApiEmitterTemplateData extends PythonTemplateData
{
    public ApiEmitterTemplateData(TemplateDataContext context, PackageName packageName)
    {
        super(context);
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

    public Iterable<String> getSubpackages()
    {
        return subpackages;
    }

    public Iterable<String> getModules()
    {
        return modules;
    }

    public Iterable<String> getPackageSymbols()
    {
        return packageSymbols;
    }

    public void addSubpackage(String subpackage)
    {
        subpackages.add(subpackage);
    }

    public void addModule(ZserioType zserioType)
    {
        modules.add(zserioType.getName());
    }

    public void addPackageSymbol(String symbolName)
    {
        packageSymbols.add(symbolName);
    }

    final PackageName packageName;
    final Set<String> subpackages = new TreeSet<String>();
    final List<String> modules = new ArrayList<String>();
    final List<String> packageSymbols = new ArrayList<String>();
}
