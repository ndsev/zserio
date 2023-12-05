package zserio.extension.java.types;

import zserio.ast.PackageName;
import zserio.extension.java.JavaFullNameFormatter;

/**
 * Native Java mapping of an object array element.
 */
public final class NativeObjectArrayElement extends NativeArrayElement
{
    public NativeObjectArrayElement(PackageName packageName, String name)
    {
        super("ObjectArrayElement<" + JavaFullNameFormatter.getFullName(packageName, name) + ">");
    }
}
