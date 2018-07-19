package zserio.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * A Scope is a lexical scope which maps names to objects. A name must be unique in its scope. Each scope
 * belongs to the one package.
 */
public class Scope implements Serializable
{
    /**
     * Constructs scope within given package and sets owner to the given Zserio type.
     *
     * @param parentPackage Package of the current scope.
     * @param owner         DataScrip type which owns the current scope.
     */
    public Scope(Package parentPackage, ZserioType owner)
    {
        this.parentPackage = parentPackage;
        this.owner = owner;

        if (parentPackage != null)  // TODO this should be moved to package
            parentPackage.addScopeToLink(this);
    }

    /**
     * Constructs scope which contains symbols from both main scope and extending scope.
     *
     * @param mainScope      Main scope for the mixed scope creation.
     * @param extendingScope Another scope whose symbol table is to be merged into the current one.
     */
    public static Scope createMixedScope(Scope mainScope, Scope extendingScope)
    {
        Scope scope = new Scope(mainScope.parentPackage, mainScope.owner);
        scope.symbolTable.putAll(mainScope.symbolTable);
        scope.symbolTable.putAll(extendingScope.symbolTable);

        return scope;
    }

    /**
     * Adds a name with its corresponding object to the current scope.
     *
     * @param name   AST node which defines name in current scope.
     * @param symbol AST node which represent an object of that name.
     *
     * @throws ParserException Throws if symbol has been already defined in the current scope.
     */
    public void setSymbol(BaseTokenAST name, Object symbol) throws ParserException
    {
        final Object symbolObject = symbolTable.put(name.getText(), symbol);
        if (symbolObject != null)
            throw new ParserException(name, "'" + name.getText() + "' is already defined in this scope!");
    }

    /**
     * Returns the package containing this scope.
     *
     * @return Enclosing package.
     */
    public Package getPackage()
    {
        return parentPackage;
    }

    /**
     * Gets the Zserio type in which is defined this lexical scope.
     *
     * @return Zserio type which owns this lexical scope.
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
    public Object getSymbol(String name)
    {
        return symbolTable.get(name);
    }

    /**
     * Returns symbols available in the current state of this scope.
     *
     * @return New set of currently visible symbols.
     */
    public Set<String> copyAvailableSymbols()
    {
        return new HashSet<String>(symbolTable.keySet());
    }

    /**
     * Registers a link action to be resolved in this scope at a later stage. A
     * link action is posted for each type reference. The reference will be
     * resolved to the type object of that name.
     *
     * Note that for a field definition {@code Foo myFoo;}, the name {@code
     * myFoo} maps to a type reference, whereas the defining occurrence of the
     * name {@code Foo} maps to the type object for {@code Foo}.
     *
     * TODO This should be moved to Package
     *
     * @param act
     *            link action
     */
    public void postLinkAction(LinkAction act)
    {
        linkActions.add(act);
    }

    /**
     * Executes all link actions in the given scope.
     *
     * TODO This should be moved to Package
     *
     * @throws ParserException
     */
    public void link() throws ParserException
    {
        for (LinkAction l : linkActions)
            l.link(this);
    }

    private static final long serialVersionUID = -5373010074297029934L;

    private final Package parentPackage;
    private final ZserioType owner;

    /**
     * Symbol table containing local symbols defined within the current scope. Each symbol is mapped to
     * an Object.
     */
    private Map<String, Object> symbolTable = new HashMap<String, Object>();

    /**
     * List of link actions to be executed within this scope. All children of
     * this scope post a link action for themselves on creation. Thus when
     * linking this scope, all subscopes will be linked automatically.
     */
    private List<LinkAction> linkActions = new ArrayList<LinkAction>();
}
