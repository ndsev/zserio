package zserio.extension.cpp;

import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class UserTypeTemplateData extends CppTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioExtensionException
    {
        super(context);

        nativeType = context.getCppNativeMapper().getCppType(type);
        packageData = new PackageTemplateData(nativeType);
    }

    public String getName()
    {
        return nativeType.getName();
    }

    public String getFullName()
    {
        return nativeType.getFullName();
    }

    public PackageTemplateData getPackage()
    {
        return packageData;
    }

    private final CppNativeType nativeType;
    private final PackageTemplateData packageData;
}
