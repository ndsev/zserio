package zserio.emit.cpp;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class UserTypeTemplateData extends CppTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        super(context);

        nativeType = context.getCppNativeTypeMapper().getCppType(type);
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

    protected CppNativeType getNativeType()
    {
        return nativeType;
    }

    private final CppNativeType nativeType;
    private final PackageTemplateData packageData;
}
