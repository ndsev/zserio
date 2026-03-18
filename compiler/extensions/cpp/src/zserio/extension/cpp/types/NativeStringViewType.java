package zserio.extension.cpp.types;

import java.util.Collections;

import zserio.extension.cpp.CppFullNameFormatter;
import zserio.extension.cpp.CppLiteralFormatter;

/**
 * Native C++ StringView type mapping.
 */
public final class NativeStringViewType extends NativeRuntimeType
{
    public NativeStringViewType()
    {
        // note that we use StringView for constant strings and thus it's a simple type
        super("StringView", Collections.singleton("zserio/StringView.h"), true);
    }

    public String formatLiteral(String value)
    {
        return CppFullNameFormatter.getFullName(getPackageName(), "makeStringView") + "(" +
                CppLiteralFormatter.formatStringLiteral(value) + ")";
    }
}
