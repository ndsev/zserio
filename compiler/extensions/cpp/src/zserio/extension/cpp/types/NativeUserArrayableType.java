package zserio.extension.cpp.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import zserio.ast.PackageName;

/**
 * Native C++ user arrayable type mapping.
 */
public class NativeUserArrayableType extends NativeUserType implements CppNativeArrayableType
{
    public NativeUserArrayableType(PackageName packageName, String name, String includeFileName,
            boolean isSimpleType, NativeArrayTraits arrayTraits)
    {
        super(packageName, name, isSimpleType, arrayTraits.getSystemIncludeFiles(),
                makeUserIncludes(includeFileName, arrayTraits));

        this.arrayTraits = arrayTraits;
    }

    public static Collection<String> makeUserIncludes(String includeFileName, NativeArrayTraits arrayTraits)
    {
        final Collection<String> includes = new ArrayList<String>();
        includes.add(includeFileName);
        includes.addAll(arrayTraits.getUserIncludeFiles());
        return includes;
    }

    @Override
    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayTraits arrayTraits;
}
