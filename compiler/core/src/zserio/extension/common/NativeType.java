package zserio.extension.common;

/**
 * Interface for native types used by extensions.
 */
public interface NativeType
{
    /**
     * Returns the full name of the type.
     *
     * @return The name of the type including package or namespace name (if the type is contained in one).
     */
    public String getFullName();

    /**
     * Returns the short name of the type.
     *
     * @return The name of the type excluding package or namespace name.
     */
    public String getName();
}
