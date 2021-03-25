package zserio.extension.common.sql.types;

/**
 * Interface for SQLite native types used by extensions.
 */
public interface SqlNativeType
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

    /**
     * Returns traditional SQL type name.
     *
     * We used only SQLite types which are not historically the traditional SQL types. As an example, the
     * traditional name for SQLite type "TEXT" is "VARCHAR".
     *
     * @return Traditional SQL type name.
     */
    public String getTraditionalName();
}
