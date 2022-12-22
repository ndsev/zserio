package zserio.extension.cpp;

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for PyBind11Emitter.
 */
public class PyBind11TemplateData extends CppTemplateData
{
    public PyBind11TemplateData(TemplateDataContext context, PackageName packageName)
    {
        super(context);
        packageData = new PackageTemplateData(packageName);
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
    }

    public Iterable<PackageTemplateData> getSubpackages()
    {
        return subpackages;
    }

    public Iterable<NativeTypeInfoTemplateData> getPackageTypes()
    {
        return packageTypes;
    }

    public Iterable<CppNativeSymbol> getPackageSymbols()
    {
        return packageSymbols;
    }

    void addSubpackage(PackageName subpackageName)
    {
        // please note that adding of the same subpackage can be called several times
        subpackages.add(new PackageTemplateData(subpackageName));
    }

    void addCppType(CppNativeType nativeType, ZserioType baseType) throws ZserioExtensionException
    {
        packageTypes.add(new NativeTypeInfoTemplateData(nativeType, baseType));
        addCppIncludesForType(nativeType);
    }

    void addCppSymbol(CppNativeSymbol nativeSymbol)
    {
        packageSymbols.add(nativeSymbol);
        addCppUserIncludes(Collections.singleton(nativeSymbol.getIncludeFile()));
    }

    // TODO[Mi-L@]: not good enough
    private static class PackageTemplateDataComparator implements Comparator<PackageTemplateData>
    {
        @Override
        public int compare(PackageTemplateData lhs, PackageTemplateData rhs)
        {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    private final PackageTemplateData packageData;
    private final Set<PackageTemplateData> subpackages = new TreeSet<PackageTemplateData>(
            new PackageTemplateDataComparator());
    private final List<NativeTypeInfoTemplateData> packageTypes = new ArrayList<NativeTypeInfoTemplateData>();
    private final List<CppNativeSymbol> packageSymbols = new ArrayList<CppNativeSymbol>();
}
