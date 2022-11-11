package zserio.extension.java.types;

/**
 * Native Java string type mapping.
 */
public class NativeStringType extends NativeArrayableType
{
    public NativeStringType()
    {
        super(JAVA_LANG_PACKAGE, "String",
                new NativeRawArray("StringRawArray"),
                new NativeArrayTraits("StringArrayTraits"),
                new NativeObjectArrayElement(JAVA_LANG_PACKAGE, "String"));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
