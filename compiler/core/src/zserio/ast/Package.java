package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.tools.HashUtil;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for one package defined in the language.
 *
 * Package is represented by one translation unit (one source file).
 */
public class Package extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param packageName Name of the package.
     * @param imports     List of all imports defined in the package.
     * @param localTypes  Map of all available local types defined in the package.
     * @param docComment  Documentation comment belonging to this node.
     */
    public Package(AstLocation location, PackageName packageName, List<Import> imports, DocComment docComment)
    {
        super(location, docComment);

        this.packageName = packageName;
        this.imports = imports;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitPackage(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (Import packageImport : imports)
            packageImport.accept(visitor);

        for (AstNode symbol : localSymbols.values())
            symbol.accept(visitor);

        for (ZserioType type : localTypes.values())
            type.accept(visitor);
    }

    /**
     * Gets name of the package.
     *
     * @return Package name.
     */
    public PackageName getPackageName()
    {
        return packageName;
    }

    /**
     * Gets imports which are defined in this package.
     *
     * @return List of all imports defined in this package.
     */
    public List<Import> getImports()
    {
        return Collections.unmodifiableList(imports);
    }

    /**
     * Adds a new type to this package.
     *
     * @param type Zserio type to add.
     */
    void addType(ZserioType type)
    {
        final ZserioType addedType = localTypes.put(type.getName(), type);
        if (addedType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    type.getLocation(), "'" + type.getName() + "' is already defined in this package!");
            stackedException.pushMessage(addedType.getLocation(), "    First defined here");
            throw stackedException;
        }
    }

    /**
     * Adds a new global symbol to this package.
     *
     * @param name   Symbol name.
     * @param symbol Symbol AST node.
     */
    void addSymbol(String name, AstNode symbol)
    {
        final AstNode addedSymbol = localSymbols.put(name, symbol);
        if (addedSymbol != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    symbol.getLocation(), "'" + name + "' is already defined in this package!");
            stackedException.pushMessage(addedSymbol.getLocation(), "    First defined here");
            throw stackedException;
        }
    }

    /**
     * Gets Zserio type for given type name with its package if it's visible for this package.
     *
     * @param ownerNode   AST node which holds type to resolve (used for ParserException).
     * @param packageName Package name of the type to resolve.
     * @param typeName    Type name to resolve.
     *
     * @return Zserio type if given type name is visible for this package or null if given type name is unknown.
     */
    ZserioType getVisibleType(AstNode ownerNode, PackageName packageName, String typeName)
    {
        final List<ZserioType> foundTypes = getAllVisibleTypes(packageName, typeName);
        final int numFoundTypes = foundTypes.size();
        if (numFoundTypes > 1)
        {
            final ParserStackedException stackedException = new ParserStackedException(ownerNode.getLocation(),
                    "Ambiguous type reference '" + typeName + "'");
            for (ZserioType foundType : foundTypes)
                stackedException.pushMessage(foundType.getLocation(), "    Found here");
            throw stackedException;
        }

        return (numFoundTypes == 1) ? foundTypes.get(0) : null;
    }

    /**
     * Gets a global symbol if it's visible in this package.
     *
     * This method does not throw exception in case of ambiguous symbol. It just returns null in this case.
     *
     * @param ownerNode   AST node which holds symbol to resolve (used for ParserException).
     * @param packageName Package name where the symbol is defined.
     * @param symbolName  Symbol name to resolve.
     *
     * @return Symbol AST node if the symbol is visible in this package or null if the symbol is unknown.
     */
    AstNode getVisibleSymbol(AstNode ownerNode, PackageName packageName, String symbolName)
    {
        final List<AstNode> foundSymbols = getAllVisibleSymbols(packageName, symbolName);
        final int numFoundTypes = foundSymbols.size();
        if (numFoundTypes > 1)
        {
            final ParserStackedException stackedException = new ParserStackedException(ownerNode.getLocation(),
                    "Ambiguous symbol '" + symbolName + "'");
            for (AstNode foundSymbol : foundSymbols)
                stackedException.pushMessage(foundSymbol.getLocation(), "    Found here");
            throw stackedException;
        }
        return (numFoundTypes == 1) ? foundSymbols.get(0) : null;
    }

    /**
     * Imports types to this package.
     *
     * This method resolves all imports which belong to this package
     *
     * @param packageNameMap Map of all available package name to the package object.
     */
    void processImports(Map<PackageName, Package> packageNameMap)
    {
        for (Import importedNode : imports)
        {
            final PackageName importedPackageName = importedNode.getImportedPackageName();
            final Package importedPackage = packageNameMap.get(importedPackageName);
            if (importedPackage == null)
            {
                // imported package has not been found => this could happen only for default packages
                throw new ParserException(importedNode, "Default package cannot be imported!");
            }

            final String importedName = importedNode.getImportedName();
            if (importedName == null)
            {
                // this is package import
                if (importedPackages.contains(importedPackage))
                    ZserioToolPrinter.printWarning(importedNode, "Duplicated import of package '" +
                            importedPackageName.toString() + "'.");

                // check redundant single imports
                final List<SingleImport> redundantSingleImports = new ArrayList<SingleImport>();
                for (SingleImport singleImport : singleImports)
                {
                    if (singleImport.getPackage().getPackageName().equals(importedPackageName))
                    {
                        ZserioToolPrinter.printWarning(importedNode, "Import of package '" +
                                importedPackageName.toString() + "' overwrites single import of '" +
                                ZserioTypeUtil.getFullName(importedPackageName, singleImport.getName()) + "'.");
                        redundantSingleImports.add(singleImport);
                    }
                }

                // remove all redundant single imports to avoid ambiguous error
                for (SingleImport redundantSingleImport : redundantSingleImports)
                    singleImports.remove(redundantSingleImport);

                importedPackages.add(importedPackage);
            }
            else
            {
                // this is single import
                if (importedPackages.contains(importedPackage))
                {
                    ZserioToolPrinter.printWarning(importedNode, "Single import of '" +
                            ZserioTypeUtil.getFullName(importedPackageName, importedName) +
                            "' already covered by package import.");
                    // don't add it to imported single imports because this type would become ambiguous
                }
                else
                {
                    final SingleImport singleImport = new SingleImport(importedPackage,
                            importedName);
                    if (singleImports.contains(singleImport))
                        ZserioToolPrinter.printWarning(importedNode, "Duplicated import of '" +
                                ZserioTypeUtil.getFullName(importedPackageName, importedName) + "'.");

                    final AstNode importedSymbol = importedPackage.getLocalSymbol(importedPackageName,
                            importedName);
                    if (importedSymbol == null)
                    {
                        final ZserioType importedZserioType = importedPackage.getLocalType(importedPackageName,
                                importedName);
                        if (importedZserioType == null)
                            throw new ParserException(importedNode, "Unresolved import of '" +
                                    ZserioTypeUtil.getFullName(importedPackageName, importedName) + "'!");
                    }

                    singleImports.add(singleImport);
                }
            }
        }
    }

    /** Checks the package. */
    void check()
    {
        PackageIdentifierValidator validator = new PackageIdentifierValidator();
        for (ZserioType localType : localTypes.values())
            validator.validateTypeName(localType);
        for (Map.Entry<String, AstNode> localSymbolEntry : localSymbols.entrySet())
            validator.validateSymbol(localSymbolEntry.getKey(), localSymbolEntry.getValue());
    }

    /**
     * Gets local type matching the given name.
     *
     * @param name Name of the local type to find.
     *
     * @return Local type matching the given name or null.
     */
    ZserioType getLocalType(String name)
    {
        return getLocalType(PackageName.EMPTY, name);
    }

    /**
     * Gets explicit instantiation request visible in the package which matches to the given template and
     * arguments.
     *
     * @param template          Template which is being instantiated.
     * @param templateArguments Current template arguments.
     *
     * @return Matching instantiation type or null.
     */
    InstantiateType getVisibleInstantiateType(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        if (visibleInstantiateTypeMap == null)
        {
            // lazy initialization to optimize searching
            visibleInstantiateTypeMap = new HashMap<InstantiateTypeMapKey, InstantiateType>();

            for (ZserioType type : localTypes.values())
                fillVisibleIntantiateTypeMap(type);

            for (Package importedPackage : importedPackages)
                for (ZserioType type : importedPackage.localTypes.values())
                    fillVisibleIntantiateTypeMap(type);

            for (SingleImport singleImport : singleImports)
            {
                final Package singleTypePackage = singleImport.getPackage();
                final ZserioType type = singleTypePackage.localTypes.get(singleImport.getName());
                fillVisibleIntantiateTypeMap(type);
            }
        }

        return visibleInstantiateTypeMap.get(new InstantiateTypeMapKey(template, templateArguments));
    }

    private void fillVisibleIntantiateTypeMap(ZserioType type)
    {
        if (type instanceof InstantiateType)
        {
            final InstantiateType instantiateType = (InstantiateType)type;
            final TemplatableType instantiateTemplate = instantiateType.getTemplate();
            final List<TemplateArgument> instantiateTemplateArguments =
                    instantiateType.getTypeReference().getTemplateArguments();
            final InstantiateTypeMapKey key =
                    new InstantiateTypeMapKey(instantiateTemplate, instantiateTemplateArguments);
            final InstantiateType previousInstantiateType = visibleInstantiateTypeMap.put(key, instantiateType);
            if (previousInstantiateType != null)
            {
                final ParserStackedException stackedException = new ParserStackedException(
                        instantiateType.getLocation(), "Ambiguous request to instantiate template '" +
                        ZserioTypeUtil.getReferencedFullName(instantiateType.getTypeReference()) + "'!");
                stackedException.pushMessage(previousInstantiateType.getLocation(),
                        "    First requested here");
                throw stackedException;
            }
        }
    }

    private List<ZserioType> getAllVisibleTypes(PackageName packageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        final ZserioType foundLocalType = getLocalType(packageName, typeName);
        if (foundLocalType != null)
            foundTypes.add(foundLocalType);
        foundTypes.addAll(getTypesFromSingleImports(packageName, typeName));

        if (foundTypes.isEmpty())
        {
            // because we must check for ambiguous types, we must collect all found types
            foundTypes.addAll(getTypesFromImportedPackages(packageName, typeName));
        }

        return foundTypes;
    }

    private ZserioType getLocalType(PackageName typePackageName, String typeName)
    {
        if (!typePackageName.isEmpty() && !typePackageName.equals(getPackageName()))
            return null;

        return localTypes.get(typeName);
    }

    private List<ZserioType> getTypesFromImportedPackages(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        for (Package importedPackage : importedPackages)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            final ZserioType importedType = importedPackage.getLocalType(typePackageName, typeName);
            if (importedType != null)
                foundTypes.add(importedType);
        }

        return foundTypes;
    }

    private List<ZserioType> getTypesFromSingleImports(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        for (SingleImport importedSingleType : singleImports)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            if (typeName.equals(importedSingleType.getName()))
            {
                final Package importedPackage = importedSingleType.getPackage();
                final ZserioType importedType = importedPackage.getLocalType(typePackageName, typeName);
                if (importedType != null)
                    foundTypes.add(importedType);
            }
        }

        return foundTypes;
    }

    private List<AstNode> getAllVisibleSymbols(PackageName packageName, String symbolName)
    {
        final List<AstNode> foundSymbols = new ArrayList<AstNode>();
        final AstNode foundLocalSymbol = getLocalSymbol(packageName, symbolName);
        if (foundLocalSymbol != null)
            foundSymbols.add(foundLocalSymbol);
        foundSymbols.addAll(getSymbolsFromSingleImports(packageName, symbolName));

        if (foundSymbols.isEmpty())
        {
            // because we must check for ambiguous types, we must collect all found types
            foundSymbols.addAll(getSymbolsFromImportedPackages(packageName, symbolName));
        }

        return foundSymbols;
    }

    private AstNode getLocalSymbol(PackageName packageName, String symbolName)
    {
        if (!packageName.isEmpty() && !packageName.equals(getPackageName()))
            return null;

        return localSymbols.get(symbolName);
    }

    private List<AstNode> getSymbolsFromImportedPackages(PackageName packageName, String symbolName)
    {
        final List<AstNode> foundSymbols = new ArrayList<AstNode>();
        for (Package importedPackage : importedPackages)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            final AstNode importedSymbol = importedPackage.getLocalSymbol(packageName, symbolName);
            if (importedSymbol!= null)
                foundSymbols.add(importedSymbol);
        }

        return foundSymbols;
    }

    private List<AstNode> getSymbolsFromSingleImports(PackageName packageName, String symbolName)
    {
        final List<AstNode> foundSymbols = new ArrayList<AstNode>();
        for (SingleImport singleImport : singleImports)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            if (symbolName.equals(singleImport.getName()))
            {
                final Package importedPackage = singleImport.getPackage();
                final AstNode importedSymbol = importedPackage.getLocalSymbol(packageName, symbolName);
                if (importedSymbol != null)
                    foundSymbols.add(importedSymbol);
            }
        }

        return foundSymbols;
    }

    private static class SingleImport implements Comparable<SingleImport>
    {
        public SingleImport(Package pkg, String name)
        {
            this.pkg = pkg;
            this.name = name;
        }

        @Override
        public int compareTo(SingleImport other)
        {
            final int result = name.compareTo(other.name);
            if (result != 0)
                return result;

            return pkg.getPackageName().compareTo(other.pkg.getPackageName());
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof SingleImport)
                return compareTo((SingleImport)other) == 0;

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, name);
            hash = HashUtil.hash(hash, pkg.getPackageName());

            return hash;
        }

        public Package getPackage()
        {
            return pkg;
        }

        public String getName()
        {
            return name;
        }

        private final Package pkg;
        private final String name;
    }

    private static class InstantiateTypeMapKey
    {
        public InstantiateTypeMapKey(TemplatableType template, List<TemplateArgument> templateArguments)
        {
            this.templateFullName = ZserioTypeUtil.getFullName(template);
            this.templateArguments = templateArguments;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof InstantiateTypeMapKey))
                return false;

            if (this != other)
            {
                final InstantiateTypeMapKey otherKey = (InstantiateTypeMapKey)other;
                if (!templateFullName.equals(otherKey.templateFullName))
                    return false;

                if (!templateArguments.equals(otherKey.templateArguments))
                    return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, templateFullName);
            hash = HashUtil.hash(hash, templateArguments);

            return hash;
        }

        private final String templateFullName;
        private final List<TemplateArgument> templateArguments;
    }

    private final PackageName packageName;
    private final List<Import> imports;

    // this must be a LinkedHashMap because of 'Cyclic dependency' error checked in ZserioAstTypeResolver
    private final LinkedHashMap<String, ZserioType> localTypes = new LinkedHashMap<String, ZserioType>();

    // global symbols defined within this package
    private final Map<String, AstNode> localSymbols = new LinkedHashMap<String, AstNode>();

    private final Set<Package> importedPackages = new HashSet<Package>();
    // this must be a TreeSet because of 'Ambiguous type reference' error checked in getVisibleType()
    private final Set<SingleImport> singleImports = new TreeSet<SingleImport>();

    private Map<InstantiateTypeMapKey, InstantiateType> visibleInstantiateTypeMap = null;
}
