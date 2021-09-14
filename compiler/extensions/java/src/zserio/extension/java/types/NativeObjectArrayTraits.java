package zserio.extension.java.types;

import zserio.ast.PackageName;
import zserio.extension.java.JavaFullNameFormatter;

public class NativeObjectArrayTraits extends NativeArrayTraits
{
    public NativeObjectArrayTraits(PackageName packageName, String name, boolean withWriterCode)
    {
        super((withWriterCode ? "WriteObjectArrayTraits<" : "ObjectArrayTraits<") +
                JavaFullNameFormatter.getFullName(packageName, name) + ">");
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
