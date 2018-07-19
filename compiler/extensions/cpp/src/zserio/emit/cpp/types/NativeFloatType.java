package zserio.emit.cpp.types;

import java.util.ArrayList;

public class NativeFloatType extends CppNativeType
{
    public NativeFloatType()
    {
        super(new ArrayList<String>(), "float", true);
    }
}
