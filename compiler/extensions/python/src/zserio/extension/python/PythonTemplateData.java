package zserio.extension.python;

import java.util.LinkedHashSet;
import java.util.TreeSet;

import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.extension.python.types.NativeUserType;
import zserio.extension.python.types.PythonNativeType;

/**
 * Base class for all Python template data for FreeMarker.
 */
public class PythonTemplateData implements ImportCollector
{
    public PythonTemplateData(TemplateDataContext context)
    {
        withWriterCode = context.getWithWriterCode();
        withTypeInfoCode = context.getWithTypeInfoCode();
        withCodeComments = context.getWithCodeComments();
        generatorDescription = context.getGeneratorDescription();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCode;
    }

    public boolean getWithCodeComments()
    {
        return withCodeComments;
    }

    public Iterable<String> getPackageImports()
    {
        return packageImports;
    }

    public Iterable<String> getTypeImports()
    {
        return typeImports;
    }

    public Iterable<String> getSymbolImports()
    {
        return symbolImports;
    }

    @Override
    public void importPackage(String packageName)
    {
        packageImports.add(packageName);
    }

    @Override
    public void importSymbol(PythonNativeSymbol nativeSymbol)
    {
        final String symbolImport = PythonFullNameFormatter.getModuleFullName(nativeSymbol);
        symbolImports.add(symbolImport);
    }

    @Override
    public void importType(PythonNativeType nativeType)
    {
        if (nativeType instanceof NativeUserType)
        {
            final String typeImport = PythonFullNameFormatter.getModuleFullName(nativeType);
            typeImports.add(typeImport);
        }
    }

    private final boolean withWriterCode;
    private final boolean withTypeInfoCode;
    private final boolean withCodeComments;
    private final String generatorDescription;

    // keep ordering to have system imports before application imports
    private final LinkedHashSet<String> packageImports = new LinkedHashSet<String>();

    private final TreeSet<String> typeImports = new TreeSet<String>();
    private final TreeSet<String> symbolImports = new TreeSet<String>();
}
