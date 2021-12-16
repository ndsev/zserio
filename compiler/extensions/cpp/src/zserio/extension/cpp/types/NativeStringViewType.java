package zserio.extension.cpp.types;

import zserio.extension.cpp.CppFullNameFormatter;
import zserio.extension.cpp.CppLiteralFormatter;

public class NativeStringViewType extends NativeRuntimeType
{
    public NativeStringViewType()
    {
        // note that we use StringView for constant strings and thus it's a simple type
        super("StringView", "zserio/StringView.h", true);
    }

    public String formatLiteral(String value)
    {
        return CppFullNameFormatter.getFullName(getPackageName(), "makeStringView") +
                "(" + CppLiteralFormatter.formatStringLiteral(value) + ")";
    }
}
