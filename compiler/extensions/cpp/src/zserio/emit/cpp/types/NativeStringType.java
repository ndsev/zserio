package zserio.emit.cpp.types;

import java.util.Arrays;

public class NativeStringType extends CppNativeType
{
    public NativeStringType()
    {
        super(Arrays.asList("std"), "string", false);
        addSystemIncludeFile("string");
    }
}
