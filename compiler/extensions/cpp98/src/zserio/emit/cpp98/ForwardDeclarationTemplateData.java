package zserio.emit.cpp98;

import zserio.ast.PackageName;
import zserio.emit.cpp98.CppTemplateData.PackageTemplateData;
import zserio.emit.cpp98.types.CppNativeType;
import zserio.tools.HashUtil;

public class ForwardDeclarationTemplateData implements Comparable<ForwardDeclarationTemplateData>
{
    public ForwardDeclarationTemplateData(CppNativeType nativeType)
    {
        packageName = nativeType.getPackageName();
        cppTypeNameOnly = nativeType.getName();

        packageData = new PackageTemplateData(packageName);
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
    }

    public String getCppTypeNameOnly()
    {
        return cppTypeNameOnly;
    }

    @Override
    public int compareTo(ForwardDeclarationTemplateData other)
    {
        int result = packageName.compareTo(other.packageName);

        if (result == 0)
            result = cppTypeNameOnly.compareTo(other.cppTypeNameOnly);

        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof ForwardDeclarationTemplateData)
        {
            return compareTo((ForwardDeclarationTemplateData)other) == 0;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, packageName);
        hash = HashUtil.hash(hash, cppTypeNameOnly);
        return hash;
    }

    private final PackageName packageName;
    private final String cppTypeNameOnly;

    private final PackageTemplateData packageData;
}
