package zserio.ast;

/**
 * Interface for a AST node which can be stored in the package as a symbol.
 */
public interface PackageSymbol extends ScopeSymbol
{
    /**
     * Gets the package in which this symbol is defined.
     *
     * @return The package in which this symbol is defined.
     */
    public Package getPackage();
}
