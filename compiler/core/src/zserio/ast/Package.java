package zserio.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;
import zserio.tools.PackageManager;
import zserio.tools.StringJoinUtil;

/**
 * This class represents a Zserio package which provides a separate lexical
 * scope for type names and other names.
 *
 * By default, only type names defined in the current package are visible. Names
 * from other packages can be made visible with an <code>import</code>
 * declaration.
 *
 * The root package is the one passed to the Zserio compiler. This package
 * is special in the sense that some generated Java classes (e.g. __Visitor)
 * which are required in all generated classes are put into the Java package
 * associated to the Zserio root package.
 */
public class Package implements Serializable
{
    public Package(PackageManager packageManager, String packageName)
    {
        this.packageManager = packageManager;
        localTypes = new HashMap<String, ZserioType>();
        importedTypesFull = new HashMap<String, ZserioType>();
        importedTypesShort = new HashMap<String, ZserioType>();
        importedPackages = new HashMap<String, Package>();
        importNodes = new ArrayList<Import>();
        this.packageName = packageName;
    }

    /**
     * Constructs a Package object for an AST node of type PACKAGE.
     *
     * @param packageNode
     *            the AST node
     */
    public Package(PackageManager packageCache, TokenAST packageNode)
    {
        this(packageCache, getPackageNameFromAstNode(packageNode));
        node = packageNode;
    }

    /**
     * Assembles package name from an AST node.
     *
     * The text is taken from the children of the package node.
     *
     * @param packageNode AST node of the import or package node.
     * @return String assembler from all the children of the node passed, joined using '.'.
     */
    private static String getPackageNameFromAstNode(AST packageNode)
    {
        StringJoinUtil.Joiner joiner = new StringJoinUtil.Joiner(SEPARATOR);
        for (AST child = packageNode.getFirstChild(); child != null; child = child.getNextSibling())
            joiner.append(child.getText());

        return joiner.toString();
    }

    /**
     * Maps each imported package and each imported type name to the corresponding
     * Package or ZserioType object.
     *
     * This uses the associated package manager to look the required packages up.
     */
    public void resolveImports() throws ParserException
    {
        final Set<String> importedSingleTypePackageNames = new HashSet<String>();
        for (Import importNode : importNodes)
        {
            final String importedTypeName = importNode.getTypeName();
            final String importedPackageName =
                    StringJoinUtil.joinStrings(importNode.getPackagePath(), SEPARATOR);
            final Package importedPackage = packageManager.lookup(importedPackageName);
            if (importedPackage == null)
                throw new ParserException(importNode, "Unknown imported package " + importedPackageName + "!");

            if (importedTypeName == null)
            {
                // this is package import
                if (importedPackages.containsKey(importedPackageName))
                    ZserioToolPrinter.printWarning(importNode, "Duplicated import of package " +
                            importedPackageName + ".");

                if (importedSingleTypePackageNames.contains(importedPackageName))
                    ZserioToolPrinter.printWarning(importNode, "Import of package " + importedPackageName +
                            " overwrites some single type imports.");

                importedPackages.put(importedPackageName, importedPackage);
            }
            else
            {
                // this is single type import
                importedSingleTypePackageNames.add(importedPackageName);
                final String importedFullTypeName = StringJoinUtil.joinStrings(importedPackageName,
                        importedTypeName, SEPARATOR);
                if (importedTypesFull.containsKey(importedFullTypeName))
                    ZserioToolPrinter.printWarning(importNode, "Duplicated import of type " +
                            importedFullTypeName + ".");

                if (importedPackages.containsKey(importedPackageName))
                    ZserioToolPrinter.printWarning(importNode, "Single type " + importedTypeName +
                            " imported already by package import.");

                final ZserioType importedZserioType = importedPackage.getLocalType(importedTypeName);
                if (importedZserioType == null)
                    throw new ParserException(importNode, "Unknown type " + importedTypeName +
                            " in imported package " + importedPackageName + "!");

                importedTypesFull.put(importedFullTypeName, importedZserioType);
                addShortNameForImportedType(importedTypeName, importedZserioType);
            }
        }
    }

    private void addShortNameForImportedType(String name, ZserioType type)
    {
        if (importedTypesShort.containsKey(name))
        {
            // type name is ambiguous, set target to null so that we can
            // catch this issue if the type is requested using its short name
            importedTypesShort.put(name, null);
        }
        else
        {
            importedTypesShort.put(name, type);
        }
    }

    /**
     * Stores a type in the local types map.
     *
     * @param name
     *            AST node for type name
     * @param typeNode
     *            AST node for type definition
     *
     * @throws ParserException
     */
    public void setType(BaseTokenAST name, Object typeNode) throws ParserException
    {
        ZserioType type = (ZserioType) typeNode;
        final Object typeObject = localTypes.put(name.getText(), type);
        if (typeObject != null)
            throw new ParserException(name, "'" + name.getText() + "' is already defined in this package!");
    }

    /**
     * Finds a local type by name.
     *
     * @param name
     *            local name within the current package
     * @return type object
     */
    public ZserioType getLocalType(String name)
    {
        return localTypes.get(name);
    }

    /**
     * Gets local type names.
     *
     * @return Set of local type names known in this package.
     */
    public Set<String> getLocalTypeNames()
    {
        return localTypes.keySet();
    }

    /**
     * Adds an import node to the list of imported packages or classes. A warning is
     * emitted for multiple imports of the same package or class.
     *
     * @param n
     *            IMPORT AST node
     */
    public void addImport(Import importNode)
    {
        importNodes.add(importNode);
    }

    /**
     * Helper class that holds the two parts of a split type name: the package and the proper type name parts.
     */
    private static class SplitName
    {
        public SplitName(String packageName, String typeName)
        {
            this.packageName = packageName;
            this.typeName = typeName;
        }

        public String packageName;
        public String typeName;
    }

    /**
     * Split type name into package and proper type name parts.
     *
     * The division is done based on the known package names.
     */
    private SplitName splitTypeName(String typeName)
    {
        String packageName = "";

        int pos = -1;
        int lastpos = -1;
        boolean done = false;
        while (!done)
        {
            pos = typeName.indexOf('.', pos + 1);
            if (pos == -1)
            {
                done = true;
            }
            else if (packageManager.lookup(typeName.substring(0, pos)) != null)
            {
                lastpos = pos;
            }
        }

        if (lastpos != -1)
        {
            packageName = typeName.substring(0, lastpos);
            typeName = typeName.substring(lastpos + 1);
        }

        return new SplitName(packageName, typeName);
    }

    private ZserioType getTypeFromDirectImports(String name) throws ParserException
    {
        ZserioType type = importedTypesFull.get(name);
        if (type == null)
        {
            // try looking up the name in the "short" type name map
            if (importedTypesShort.containsKey(name))
            {
                type = importedTypesShort.get(name);
                if (type == null)
                {
                    // map entry is set to null (see addShortNameForImportedType())
                    // if a short name for an imported type is ambiguous
                    throw new ParserException(this.node, "ambiguous type reference '" + name + "'");
                }
            }
        }

        return type;
    }

    private ZserioType getTypeFromPackageImports(SplitName splitName) throws ParserException
    {
        return getTypeFromOnePackageImports(new HashSet<String>(), this, splitName);
    }

    private ZserioType getTypeFromOnePackageImports(Set<String> visitedPackages, Package zserioPackage,
                                                        SplitName splitName) throws ParserException
    {
        ZserioType result = null;
        for (Package p: zserioPackage.importedPackages.values())
        {
            if (!visitedPackages.contains(p.packageName))
            {
                visitedPackages.add(p.packageName);

                ZserioType externalType = p.getLocalType(splitName.typeName);
                if (externalType == null)
                    externalType = getTypeFromOnePackageImports(visitedPackages, p, splitName);

                if (externalType != null)
                {
                    if (splitName.packageName.length() != 0)
                    {
                        if (splitName.packageName.equals(externalType.getPackage().getPackageName()))
                            return externalType; // exact match
                    }
                    else
                    {
                        if (result == null)
                            result = externalType; // don't exit the loop yet - go on to check for ambiguities
                        else
                            throw new ParserException(this.node, "ambiguous type reference '" +
                                                      splitName.typeName + "'");
                    }
                }
            }
        }

        return result;
    }

    /**
     * Finds a type with a given name. If the type is not a local type, the type
     * will be looked up by its local name within each imported package.
     *
     * An error is logged if the type name exists in more than one imported
     * package, but only if the type name does not exist in the current package
     *
     * @todo This method is called directly from emitters and it throws ParserException!
     *
     * @param name
     *            local type name
     */
    public ZserioType getType(String name) throws ParserException
    {
        SplitName splitName = splitTypeName(name);
        ZserioType result = null;

        if (splitName.packageName.length() == 0 || splitName.packageName.equals(getPackageName()))
            result = getLocalType(splitName.typeName);

        if (result == null)
            result = getTypeFromDirectImports(name);

        if (result == null)
            result = getTypeFromPackageImports(splitName);

        return result;
    }

    /**
     * Finds a local package by name.
     *
     * @param name
     *            local name within the current package
     * @return package object
     */
    public Package getLocalPackage(String name)
    {
        return packageManager.lookup(name);
    }

    /**
     * Returns the fully qualified name of this package
     *
     * @return e.g. "com.acme.foo.bar"
     */
    public String getPackageName()
    {
        return packageName;
    }

    /**
     * Returns the reverse fully qualified name of this package
     *
     * @return e.g. "bar.foo.acme.com"
     */
    public String getReversePackageName()
    {
        if (reversePackageName == null)
            reversePackageName = StringJoinUtil.joinStrings(getReversePackagePath(), SEPARATOR);

        return reversePackageName;
    }

    /**
     * Returns this list of subpackage names for the current package.
     *
     * @return e.g. ["com", "acme", "foo", "bar"]
     */
    public List<String> getPackagePath()
    {
        if (packagePath == null)
            evaluatePackagePaths();
        return packagePath;
    }

    /**
     * Returns this list of reverse subpackage names for the current package.
     *
     * @return e.g. ["bar", "foo", "acme", "com"]
     */
    public List<String> getReversePackagePath()
    {
        if (reversePackagePath == null)
            evaluatePackagePaths();
        return reversePackagePath;
    }

    private void evaluatePackagePaths()
    {
        if (packagePath == null)
        {
            if (this == PackageManager.get().defaultPackage)
            {
                packagePath = new ArrayList<String>();
                reversePackagePath = new ArrayList<String>();
            }
            else
            {
                packagePath = java.util.Arrays.asList(getPackageName().split(SEPARATOR_REGEX));
                reversePackagePath = new ArrayList<String>(packagePath);
                Collections.reverse(reversePackagePath);
            }
        }
    }

    public void fillTypeFieldList(String name, List<ZserioType> typeList, List<String> fieldList)
    {
        for (String typeName: localTypes.keySet())
        {
            String matchName = null;

            final String fullTypeName = getPackageName() + SEPARATOR + typeName;
            if (name.startsWith(fullTypeName + SEPARATOR) || name.equals(fullTypeName))
            {
                // name is already fully-specified
                matchName = name;
            }
            else
            {
                // test if it exists in this package
                final String fname = getPackageName() + SEPARATOR + name;

                if (fname.startsWith(fullTypeName + SEPARATOR) || fname.equals(fullTypeName))
                    matchName = fname;
            }

            if (matchName != null)
            {
                // found in this package as matchName
                final String fieldPart = (matchName.length() > (fullTypeName.length()+1 /*for '.'*/)) ?
                                     matchName.substring(fullTypeName.length()+1) :
                                     "";

                if (matchName.equals(name))
                {
                    // it was an exact match
                    typeList.clear();
                    fieldList.clear();
                }

                typeList.add(getLocalType(typeName));
                fieldList.add(fieldPart); // fieldPart might be an empty string ("")

                if (matchName.equals(name))
                {
                    // exact match, no further search
                    return;
                }
            }
            // else: no match
        }
    }

    /**
     * Links owned scopes.
     */
    public void link() throws ParserException
    {
        for (Scope s : scopesToLink)
            s.link();
    }

    /**
     * Resolves subtypes.
     *
     * @throws ParserException When cyclic definition is detected.
     */
    public void resolveSubtypes() throws ParserException
    {
        for (ZserioType type : localTypes.values())
        {
            if (type instanceof Subtype)
                ((Subtype)type).resolve();
        }
    }

    /**
     * Adds scope within this package to be linked.
     *
     * @param scope Scope within this package.
     */
    public void addScopeToLink(Scope scope)
    {
        scopesToLink.add(scope);
    }


    public static final char SEPARATOR_CHAR = '.';
    public static final String SEPARATOR = String.valueOf(SEPARATOR_CHAR);
    public static final String SEPARATOR_REGEX = "\\" + SEPARATOR;

    private static final long serialVersionUID = -4965489451342750488L;

    /** The package cache that this instance is associated with. */
    private PackageManager packageManager;

    /** The PACKAGE node of the AST defining this package. Note that this can be null in case it's a special package. */
    private TokenAST node;

    /** The fully qualified package name, e.g. "com.acme.foo.bar". It's set in constructor and used to derive the other names. */
    private final String packageName;

    /** The fully qualified package name, e.g. "bar.foo.acme.com". It's lazy-initialized from packagePath. */
    private String reversePackageName;

    /** List of the package name parts, e.g. ["com", "acme", "foo", "bar"]. It's lazy-initialized from packagePath. */
    private List<String> packagePath;

    /** List of the package name parts, e.g. ["bar", "foo", "acme", "com"]. It's lazy-initialized from packagePath. */
    private List<String> reversePackagePath;

    /** Scopes withing this package to be linked. */
    private List<Scope> scopesToLink = new ArrayList<Scope>();

    /** Maps all type names defined in this package to the corresponding type. */
    private Map<String, ZserioType> localTypes;

    /**
     * Maps the fully qualified name of each single-imported type to the actual type.
     */
    private Map<String, ZserioType> importedTypesFull;

    /**
     * Maps an unqualified type name (type name without package name) of each single-imported type
     * to the actual type.
     *
     * If a name is ambiguous (e.g. import foo.baz; import bar.baz;) it's mapped to null
     * and its fully-qualified name must be used to refer to it.
     */
    private Map<String, ZserioType> importedTypesShort;

    /**
     * Maps the fully qualified name of each imported package to the
     * corresponding package. At first, the value of each entry is null, just to
     * keep track of an imported package name. In the link phase, the null value
     * is replaced by the corresponding Package object.
     */
    private Map<String, Package> importedPackages;

    private List<Import> importNodes;
}
