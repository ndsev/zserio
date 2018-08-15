package zserio.emit.cpp.types;

import java.util.ArrayList;

public class NativeDoubleType extends CppNativeType
{
    public NativeDoubleType()
    {
        super(new ArrayList<String>(), "double", true);
    }
}
