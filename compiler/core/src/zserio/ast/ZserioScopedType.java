package zserio.ast;

/**
 * The interface for all Zserio types which use their own scope.
 *
 * Scope contains symbols which are defined within given Zserio type (for example, fields for compound types).
 */
public interface ZserioScopedType extends ZserioType
{
    /**
     * Gets the scope defined by this type.
     *
     * @return The scope defined by this type.
     */
    Scope getScope();
}
