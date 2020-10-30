package zserio.ast;

/**
 * Interface for a AST node which can be stored in the package as a symbol.
 */
public interface PackageSymbol extends AstNode
{
    /**
     * Gets the package in which this symbol is defined.
     *
     * @return The package in which this symbol is defined.
     */
    public Package getPackage();

    /**
     * Gets the name of the symbol stored in the package.
     *
     * @return Name of the symbol.
     */
    public String getName();
}
