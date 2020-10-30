package zserio.ast;

import java.util.HashMap;
import java.util.Map;

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
     * Adds a symbol to the current scope.
     *
     * @param symbol Symbol to add.
     */
    public void addSymbol(ScopeSymbol symbol)
    {
        final AstNode prevSymbol = symbols.put(symbol.getName(), symbol);
        if (prevSymbol != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(symbol.getLocation(),
                    "'" + symbol.getName() + "' is already defined in this scope!");
            stackedException.pushMessage(prevSymbol.getLocation(), "    First defined here");

            throw stackedException;
        }
    }

    /**
     * Removes a symbol from the current scope.
     *
     * @param symbol Symbol to remove.
     *
     * @return Removed symbol or null if no such symbol is available.
     */
    public AstNode removeSymbol(ScopeSymbol symbol)
    {
        return symbols.remove(symbol.getName());
    }

    /**
     * Adds another scope to the scope.
     *
     * @param scope Scope to be added.
     */
    public void add(Scope addedScope)
    {
        symbols.putAll(addedScope.symbols);
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
     * @return Corresponding symbol or null if no such symbol is visible.
     */
    public ScopeSymbol getSymbol(String name)
    {
        return symbols.get(name);
    }

    private final ZserioType owner;

    /**
     * Symbol table containing local symbols defined within the current scope. The key is the symbol name.
     */
    private final Map<String, ScopeSymbol> symbols = new HashMap<String, ScopeSymbol>();
}
