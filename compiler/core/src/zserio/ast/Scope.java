package zserio.ast;

import java.util.HashMap;
import java.util.Map;

import zserio.antlr.util.ParserException;

/**
 * This class represents a lexical scope which maps symbol names to objects.
 *
 * A symbol name must be unique in its scope. Each scope belongs to the one package. Scopes are used for
 * types (called owner) which contains fields (Compound Types, SQL Table, SQL Database...). Then, field name
 * is a symbol name and object represents this field.
 *
 * Scopes are filled by ZserioAstScopeSetter.
 */
class Scope
{
    /**
     * Constructs scope within given package and sets owner to the given Zserio type.
     *
     * @param owner Zserio scoped type which owns the current scope.
     */
    public Scope(ZserioScopedType owner)
    {
        this.owner = owner;
    }

    /**
     * Copy constructor.
     *
     * @param scope Scope to construct from.
     */
    public Scope(Scope scope)
    {
        owner = scope.owner;
        add(scope);
    }

    /**
     * Adds a name with its corresponding object to the current scope.
     *
     * @param name Symbol name in current scope.
     * @param symbol AST node which represent an object of that name.
     */
    public void setSymbol(String name, AstNode node)
    {
        final Object symbolObject = symbolTable.put(name, node);
        if (symbolObject != null)
            throw new ParserException(node, "'" + name + "' is already defined in this scope!");
    }

    /**
     * Removes the symbol object for the given name.
     *
     * @param name Name in the current scope to remove.
     *
     * @return Removed symbol object or null if no such symbol is available.
     */
    public AstNode removeSymbol(String name)
    {
        return symbolTable.remove(name);
    }

    /**
     * Adds another scope to the scope.
     *
     * @param scope Scope to be added.
     */
    public void add(Scope addedScope)
    {
        symbolTable.putAll(addedScope.symbolTable);
    }

    /**
     * Gets the Zserio type in which is defined this lexical scope.
     *
     * The scope always has an owner except of expressions defined directly in the package (like const).
     * The scope is 'null' in this case.
     *
     * @return Zserio type which owns this lexical scope or null.
     */
    public ZserioType getOwner()
    {
        return owner;
    }

    /**
     * Get the symbol object for the given name.
     *
     * @param name Name of the symbol to be looked up.
     *
     * @return Corresponding symbol object or null if no such symbol is visible.
     */
    public AstNode getSymbol(String name)
    {
        return symbolTable.get(name);
    }

    private final ZserioType owner;

    /**
     * Symbol table containing local symbols defined within the current scope. Each symbol is mapped to
     * an Object.
     */
    private final Map<String, AstNode> symbolTable = new HashMap<String, AstNode>();
}
