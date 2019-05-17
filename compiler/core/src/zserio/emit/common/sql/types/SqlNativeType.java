package zserio.emit.common.sql.types;

import zserio.emit.common.NativeType;

/**
 * Interface for SQLite native types used by emitters.
 */
public interface SqlNativeType extends NativeType
{
    /**
     * Returns traditional SQL type name.
     *
     * We used only SQLite types which are not historically the traditional SQL types. As an example, the
     * traditional name for SQLite type "TEXT" is "VARCHAR".
     *
     * @return Traditional SQL type name.
     */
    String getTraditionalName();
}
