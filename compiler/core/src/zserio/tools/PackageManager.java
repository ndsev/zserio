package zserio.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;
import zserio.antlr.util.ParserException;
import zserio.ast.ZserioException;
import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.ast.TokenAST;

/** TODO */
public class PackageManager implements Serializable
{
    private static volatile PackageManager instance;
    public static PackageManager get()
    {
        if (instance == null)
        {
            instance = new PackageManager();
        }

        return instance;
    }

    public PackageManager()
    {
        // must be initialized before createSpecialPackage is called
        nameToPackage = new HashMap<String, Package>();
        nodeToPackage = new HashMap<TokenAST, Package>();

        builtInPackage = createSpecialPackage("__builtin__");
        defaultPackage = createSpecialPackage("");
        rootPackage = defaultPackage;
    }

    private Package createSpecialPackage(final String name)
    {
        Package p = new Package(this, name);
        nameToPackage.put(name, p);

        return p;
    }

    private void addPackage(Package pkg, TokenAST packageNode) throws ParserException
    {
        final String pkgName = pkg.getPackageName();

        Package p = nameToPackage.get(pkgName);
        if (p == null)
        {
            nameToPackage.put(pkgName, pkg);
            nodeToPackage.put(packageNode, pkg);
        }
        else
        {
            throw new ParserException(packageNode, "duplicate package " + pkgName);
        }
    }

    public Package createPackageFromAstNode(TokenAST packageNode) throws ParserException
    {
        Package pkg = new Package(this, packageNode);
        addPackage(pkg, packageNode);
        return pkg;
    }

    /**
     * Finds a package with a given name.
     *
     * @param packageName
     *            a fully qualified package name
     * @return the corresponding package, or null.
     */
    public Package lookup(String packageName)
    {
        return nameToPackage.get(packageName);
    }

    /**
     * Finds a package for a given AST node of type PACKAGE.
     *
     * @param node
     *            AST node
     * @return the corresponding package, or null.
     */
    public Package lookup(AST node)
    {
        return nodeToPackage.get(node);
    }

    /**
     * Sets the root package, if any.
     *
     * Root package is the package of the input source file.
     *
     * This method can be called at most once. It may not be called
     * after getRoot() has been called.
     *
     * If it's not called, getRoot() returns the default package.
     */
    public void setRoot(Package rootPackage) throws ZserioException, IllegalArgumentException
    {
        if (setRootDisabled)
        {
            // setRoot() must not be called any more (either it's been called before,
            // or getRoot() has been called
            throw new ZserioException("Internal error: PackageManager.setRoot() called in a state when it must not be called");
        }

        if (rootPackage == null)
        {
            throw new IllegalArgumentException("Root package can't be null!");
        }

        setRootDisabled = true;
        this.rootPackage = rootPackage;
    }

    /**
     * Returns the root package, i.e. the one first seen by the parser.
     *
     * @return root package
     */
    public Package getRoot()
    {
        setRootDisabled = true;
        return rootPackage;
    }

    /**
     * Executes the link actions for all packages. In each package, the package
     * imports must be resolved first.
     */
    public void linkAll() throws ParserException
    {
        for (Package p: nameToPackage.values())
        {
            p.resolveImports();
        }

        for (Package p: nameToPackage.values())
        {
            p.link();
        }

        for (Package p: nameToPackage.values())
        {
            p.resolveSubtypes();
        }
    }

    public void getTypeFieldList(String name, List<ZserioType> typeList, List<String> fieldList)
    {
        typeList.clear();
        fieldList.clear();

        for (Package p: nameToPackage.values())
        {
            p.fillTypeFieldList(name, typeList, fieldList);
        }
    }

    public Set<String> getPackageNames()
    {
        return nameToPackage.keySet();
    }

    /** System package for built-in types. */
    public final Package builtInPackage;

    /**
     * Default package for types without an explicit package declaration. TODO:
     * This was intended for backward compatibility. The implementation is
     * probably not complete, since all existing Zserio modules now have a
     * package declaration.
     */
    public final Package defaultPackage;

    private static final long serialVersionUID = -1;

    /** Map of all packages in the project. */
    private final Map<String, Package> nameToPackage;

    /** Map of all packages in the project. */
    private final Map<TokenAST, Package> nodeToPackage;

    /**
     * The root package, i.e. the one first parsed.
     * In other words: this is the package the initial source file belongs in.
     */
    private Package rootPackage;

    /**
     * A flag used to check the usage of setRoot/getRoot.
     *
     * A setRoot() can be called at most once. It may never be called after getRoot() has been called.
     * This flag is used to check this.
     */
    private boolean setRootDisabled;
}
