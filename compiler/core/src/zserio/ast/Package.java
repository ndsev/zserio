package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import zserio.tools.HashUtil;
import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for one package defined in the language.
 *
 * Package is represented by one translation unit (one source file).
 */
public final class Package extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param packageName Name of the package.
     * @param topLevelPackageName Name of the top level package given by command line.
     * @param compatibilityVersion Package compatibility version AST node (can be null).
     * @param imports List of all imports defined in the package.
     * @param docComments List of documentation comments belonging to this node.
     * @param trailingDocComments List of documentation comments which are trailing at the end of this package.
     */
    public Package(AstLocation location, PackageName packageName, PackageName topLevelPackageName,
            CompatibilityVersion compatibilityVersion, List<Import> imports,
            List<DocComment> docComments, List<DocComment> trailingDocComments)
    {
        super(location, docComments);

        this.packageName = packageName;
        this.topLevelPackageName = topLevelPackageName;
        this.compatibilityVersion = compatibilityVersion;
        this.imports = imports;
        this.trailingDocComments = trailingDocComments;
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

        if (compatibilityVersion != null)
            compatibilityVersion.accept(visitor);

        for (Import packageImport : imports)
            packageImport.accept(visitor);

        for (AstNode symbol : localSymbols.values())
            symbol.accept(visitor);

        for (DocComment docComment : trailingDocComments)
            docComment.accept(visitor);
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
     * Gets package compatibility version.
     *
     * @return Compatibility version AST node or null.
     */
    public CompatibilityVersion getCompatibilityVersion()
    {
        return compatibilityVersion;
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
     * Gets list of documentation comments which are trailing at the end of this package.
     *
     * @return List of documentation comments.
     */
    public List<DocComment> getTrailingDocComments()
    {
        return Collections.unmodifiableList(trailingDocComments);
    }

    /**
     * Adds a new global symbol to this package.
     *
     * @param symbol Package symbol to add.
     */
    void addSymbol(PackageSymbol symbol)
    {
        final AstNode addedSymbol = localSymbols.put(symbol.getName(), symbol);
        if (addedSymbol != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    symbol.getLocation(), "'" + symbol.getName() + "' is already defined in this package!");
            stackedException.pushMessage(addedSymbol.getLocation(), "    First defined here");

            throw stackedException;
        }
    }

    /**
     * Gets the top level package name given by the command line.
     *
     * @return Top level package name or empty package name if top level package was not specified.
     */
    PackageName getTopLevelPackageName()
    {
        return topLevelPackageName;
    }

    /**
     * Gets a symbol if it's visible in this package.
     *
     * @param ownerNode   AST node which holds symbol to resolve (used for ParserException).
     * @param packageName Package name where the symbol is defined.
     * @param symbolName  Symbol name to resolve.
     *
     * @return Package symbol if the symbol is visible in this package or null if the symbol is unknown.
     */
    PackageSymbol getVisibleSymbol(AstNode ownerNode, PackageName packageName, String symbolName)
    {
        final List<PackageSymbol> foundSymbols = getAllVisibleSymbols(packageName, symbolName);
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
     * Gets a symbol defined locally in the package.
     *
     * @param symbolName  Symbol name to resolve.
     *
     * @return Package symbol if given symbol name is defined locally or null if given symbol name is unknown.
     */
    PackageSymbol getLocalSymbol(String symbolName)
    {
        return getLocalSymbol(PackageName.EMPTY, symbolName);
    }

    /**
     * Resolves all imports which belong to this package.
     */
    void resolveImports(WarningsConfig warningsConfig)
    {
        for (Import importedNode : imports)
        {
            final Package importedPackage = importedNode.getImportedPackage();
            final PackageName importedPackageName = importedPackage.getPackageName();
            final PackageSymbol importedSymbol = importedNode.getImportedSymbol();
            if (importedSymbol == null)
            {
                // this is a package import
                if (importedPackages.contains(importedPackage))
                    ZserioToolPrinter.printWarning(importedNode, "Duplicated import of package '" +
                            importedPackageName.toString() + "'.",
                            warningsConfig, WarningsConfig.IMPORT);

                // check redundant single imports
                final List<PackageSymbol> redundantImportedSymbols = new ArrayList<PackageSymbol>();
                for (PackageSymbol symbol : importedSymbols)
                {
                    if (symbol.getPackage().getPackageName().equals(importedPackageName))
                    {
                        ZserioToolPrinter.printWarning(importedNode, "Import of package '" +
                                importedPackageName.toString() + "' overwrites single import of '" +
                                ZserioTypeUtil.getFullName(importedPackageName, symbol.getName()) + "'.",
                                warningsConfig, WarningsConfig.IMPORT);
                        redundantImportedSymbols.add(symbol);
                    }
                }

                // remove all redundant single imports to avoid ambiguous error
                for (PackageSymbol redundantSingleImport : redundantImportedSymbols)
                    importedSymbols.remove(redundantSingleImport);

                importedPackages.add(importedPackage);
            }
            else
            {
                // this is a single import
                final String importedName = importedSymbol.getName();
                if (importedPackages.contains(importedPackage))
                {
                    ZserioToolPrinter.printWarning(importedNode, "Single import of '" +
                            ZserioTypeUtil.getFullName(importedPackageName, importedName) +
                            "' already covered by package import.",
                            warningsConfig, WarningsConfig.IMPORT);
                    // don't add it to imported single imports because this type would become ambiguous
                }
                else
                {
                    if (importedSymbols.contains(importedSymbol))
                    {
                        ZserioToolPrinter.printWarning(importedNode, "Duplicated import of '" +
                                ZserioTypeUtil.getFullName(importedPackageName, importedName) + "'.",
                                warningsConfig, WarningsConfig.IMPORT);
                    }
                    else
                    {
                        importedSymbols.add(importedSymbol);
                    }
                }
            }
        }
    }

    /**
     * Checks the package.
     */
    void check()
    {
        if (!packageName.toString().equals(packageName.toString().toLowerCase(Locale.ENGLISH)))
            throw new ParserException(this, "Package name cannot contain upper case letters!");

        final PackageSymbolValidator packageSymbolValidator = new PackageSymbolValidator();
        for (PackageSymbol localSymbol : localSymbols.values())
            packageSymbolValidator.validate(localSymbol.getName(), localSymbol);

        final RuleIdUniqueChecker checker = new RuleIdUniqueChecker();
        accept(checker);
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

            for (PackageSymbol symbol : localSymbols.values())
                fillVisibleIntantiateTypeMap(symbol);

            for (Package importedPackage : importedPackages)
                for (PackageSymbol symbol : importedPackage.localSymbols.values())
                    fillVisibleIntantiateTypeMap(symbol);

            for (PackageSymbol importedSymbol : importedSymbols)
            {
                final Package singleTypePackage = importedSymbol.getPackage();
                final PackageSymbol symbol = singleTypePackage.localSymbols.get(importedSymbol.getName());
                fillVisibleIntantiateTypeMap(symbol);
            }
        }

        return visibleInstantiateTypeMap.get(new InstantiateTypeMapKey(template, templateArguments));
    }

    private void fillVisibleIntantiateTypeMap(PackageSymbol symbol)
    {
        if (symbol instanceof InstantiateType)
        {
            final InstantiateType instantiateType = (InstantiateType)symbol;
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

    private List<PackageSymbol> getAllVisibleSymbols(PackageName packageName, String symbolName)
    {
        final List<PackageSymbol> foundSymbols = new ArrayList<PackageSymbol>();
        final PackageSymbol foundLocalSymbol = getLocalSymbol(packageName, symbolName);
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

    private List<PackageSymbol> getSymbolsFromImportedPackages(PackageName packageName, String symbolName)
    {
        final List<PackageSymbol> foundSymbols = new ArrayList<PackageSymbol>();
        for (Package importedPackage : importedPackages)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            final PackageSymbol importedSymbol = importedPackage.getLocalSymbol(packageName, symbolName);
            if (importedSymbol!= null)
                foundSymbols.add(importedSymbol);
        }

        return foundSymbols;
    }

    private List<PackageSymbol> getSymbolsFromSingleImports(PackageName packageName, String symbolName)
    {
        final List<PackageSymbol> foundSymbols = new ArrayList<PackageSymbol>();
        for (PackageSymbol importedSymbol : importedSymbols)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            final String importedSymbolName = importedSymbol.getName();
            final PackageName importedPackageName = importedSymbol.getPackage().getPackageName();
            if (symbolName.equals(importedSymbolName) &&
                    (packageName.isEmpty() || packageName.equals(importedPackageName)))
            {
                foundSymbols.add(importedSymbol);
            }
        }

        return foundSymbols;
    }

    private PackageSymbol getLocalSymbol(PackageName packageName, String symbolName)
    {
        if (!packageName.isEmpty() && !packageName.equals(getPackageName()))
            return null;

        return localSymbols.get(symbolName);
    }

    private static final class InstantiateTypeMapKey
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
    private final PackageName topLevelPackageName;
    private final CompatibilityVersion compatibilityVersion;
    private final List<Import> imports;
    private final List<DocComment> trailingDocComments;

    // package symbols defined within this package (must be a LinkedHashMap because of 'Cyclic dependency' error
    // checked in ZserioAstTypeResolver)
    private final Map<String, PackageSymbol> localSymbols = new LinkedHashMap<String, PackageSymbol>();

    private final Set<Package> importedPackages = new HashSet<Package>();
    private final Set<PackageSymbol> importedSymbols = new HashSet<PackageSymbol>();

    private Map<InstantiateTypeMapKey, InstantiateType> visibleInstantiateTypeMap = null;
}
