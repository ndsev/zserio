package zserio.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        symbols.putAll(scope.symbols);
        additionalScopes.addAll(scope.additionalScopes);
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
     * @param addedScope Scope to be added.
     */
    public void add(Scope addedScope)
    {
        additionalScopes.add(addedScope);
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
     * Finds the symbol object together with its owner for the given name.
     *
     * @param name Name of the symbol to be looked up.
     *
     * @return FoundSymbol or null if no such symbol is visible.
     */
    public FoundSymbol findSymbol(String name)
    {
        ScopeSymbol symbol = symbols.get(name);
        if (symbol != null)
            return new FoundSymbol(symbol, owner);

        for (Scope additionalScope : additionalScopes)
        {
            final FoundSymbol foundSymbol = additionalScope.findSymbol(name);
            if (foundSymbol != null)
                return foundSymbol;
        }

        return null;
    }

    /**
     * Result of findSymbol() method.
     */
    public static class FoundSymbol
    {
        /**
         * Constructor.
         *
         * @param symbol Found symbol.
         * @param owner ZserioType which is an owner of the found symbol.
         */
        FoundSymbol(ScopeSymbol symbol, ZserioType owner)
        {
            this.symbol = symbol;
            this.owner = owner;
        }

        /**
         * Gets the found symbol.
         *
         * @return Scope symbol found by findSymbol() method.
         */
        public ScopeSymbol getSymbol()
        {
            return symbol;
        }

        /**
         * Gets owner of the found symbol.
         *
         * @return ZserioType which is an owner of the found symbol.
         */
        public ZserioType getOwner()
        {
            return owner;
        }

        private final ScopeSymbol symbol;
        private final ZserioType owner;
    }

    private final ZserioType owner;

    /**
     * Symbol table containing local symbols defined within the current scope. The key is the symbol name.
     */
    private final Map<String, ScopeSymbol> symbols = new HashMap<String, ScopeSymbol>();

    private final List<Scope> additionalScopes = new ArrayList<Scope>();
}
