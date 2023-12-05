package zserio.extension.java.types;

import zserio.ast.PackageName;
import zserio.extension.java.JavaFullNameFormatter;

/**
 * Native Java object array traits mapping.
 */
public final class NativeObjectArrayTraits extends NativeArrayTraits
{
    public NativeObjectArrayTraits(PackageName packageName, String name, boolean withWriterCode,
            boolean isPackable)
    {
        super((withWriterCode ? (isPackable ? "WritePackableObjectArrayTraits<" : "WriteObjectArrayTraits<") :
                (isPackable ? "PackableObjectArrayTraits<" : "ObjectArrayTraits<")) +
                JavaFullNameFormatter.getFullName(packageName, name) + ">");
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
