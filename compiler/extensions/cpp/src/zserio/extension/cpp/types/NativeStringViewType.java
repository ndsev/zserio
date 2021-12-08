package zserio.extension.cpp.types;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;
import zserio.extension.cpp.CppLiteralFormatter;

public class NativeStringViewType extends CppNativeType
{
    public NativeStringViewType()
    {
        // note that we use StringView for constant strings and thus it's a simple type
        super(ZSERIO_PACKAGE_NAME, "StringView", true);
        addSystemIncludeFile("zserio/StringView.h");
    }

    public String formatLiteral(String value)
    {
        return CppFullNameFormatter.getFullName(getPackageName(), "makeStringView") +
                "(" + CppLiteralFormatter.formatStringLiteral(value) + ")";
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
