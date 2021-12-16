package zserio.extension.cpp.types;

/**
 * Interface for native types which can be element of an array.
 */
public interface CppNativeArrayableType extends CppNativeType
{
    public NativeArrayTraits getArrayTraits();
}