package zserio.ast;

/**
 * Interface for a AST node which can be stored in the lexical scope as a symbol.
 */
public interface ScopeSymbol extends AstNode
{
    /**
     * Gets the name of the symbol stored in the lexical scope.
     *
     * @return Name of the symbol.
     */
    public String getName();
}
