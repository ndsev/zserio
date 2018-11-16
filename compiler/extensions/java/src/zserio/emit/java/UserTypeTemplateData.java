package zserio.emit.java;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;

public class UserTypeTemplateData extends JavaTemplateData
{
    public UserTypeTemplateData(TemplateDataContext context, ZserioType type) throws ZserioEmitException
    {
        super(context);

        final JavaNativeType javaNativeType = context.getJavaNativeTypeMapper().getJavaType(type);
        packageName = JavaFullNameFormatter.getFullName(javaNativeType.getPackageName());
        name = javaNativeType.getName();
        withWriterCode = context.getWithWriterCode();
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    private final String  packageName;
    private final String  name;
    private final boolean withWriterCode;
}
