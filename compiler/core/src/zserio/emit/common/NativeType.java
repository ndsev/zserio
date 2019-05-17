package zserio.emit.common;

/**
 * Interface for native types used by emitters.
 */
public interface NativeType
{
    /**
     * Returns the full name of the type.
     *
     * @return The name of the type including package or namespace name (if the type is contained in one).
     */
    String getFullName();

    /**
     * Returns the short name of the type.
     *
     * @return The name of the type excluding package or namespace name.
     */
    String getName();
}
