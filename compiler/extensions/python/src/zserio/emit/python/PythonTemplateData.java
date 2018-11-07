package zserio.emit.python;

import java.util.TreeSet;

import zserio.emit.python.types.PythonNativeType;

public class PythonTemplateData implements ImportCollector
{
    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public Iterable<String> getImports()
    {
        return imports;
    }

    @Override
    public void importRuntime()
    {
        imports.add("zserio");
    }

    @Override
    public void importType(PythonNativeType nativeType)
    {
        String packagePath = nativeType.getPackagePath();
        if (!packagePath.isEmpty())
            imports.add(packagePath);
    }

    private static final String generatorDescription =
            "Zserio Python extension version " + PythonExtensionVersion.VERSION_STRING;
    private final TreeSet<String> imports = new TreeSet<String>();
}
