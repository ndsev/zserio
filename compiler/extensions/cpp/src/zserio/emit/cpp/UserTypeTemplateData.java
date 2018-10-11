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
        name = nativeType.getName();
        packageData = new Package(nativeType);
    }

    public String getName()
    {
        return name;
    }

    public Package getPackage()
    {
        return packageData;
    }

    protected CppNativeType getNativeType()
    {
        return nativeType;
    }

    private final CppNativeType nativeType;
    private final String name;
    private final Package packageData;
}
