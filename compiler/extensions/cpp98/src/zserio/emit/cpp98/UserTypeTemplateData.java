package zserio.emit.cpp98;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.CppNativeType;

public class UserTypeTemplateData extends CppTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        super(context);

        nativeType = context.getCppNativeTypeMapper().getCppType(type);
        name = nativeType.getName();
        packageData = new PackageTemplateData(nativeType);
    }

    public String getName()
    {
        return name;
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
    private final String name;
    private final PackageTemplateData packageData;
}
