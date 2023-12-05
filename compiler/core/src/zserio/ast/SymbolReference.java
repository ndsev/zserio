package zserio.ast;

import java.util.Map;

import zserio.ast.Scope.FoundSymbol;

/**
 * The class represents symbol reference.
 *
 * Symbol reference can be for example
 *
 * - reference to constant ('packageName.FooConstant')
 * - reference to enumeration item ('package_name.FooEnumeration.BAR_ENUM_ITEM')
 * - reference to compound field ('package_name.FooCompoundType.field')
 * - reference to package ('root_package.sub_package')
 *
 * Symbol reference is not AST node because it is used by unparsed texts in the following situation:
 *
 * - documentation comment tag see token (@see)
 */
public final class SymbolReference
{
    /**
     * Constructor from owner token and text.
     *
     * @param ownerNode           AST token which owns the symbol reference test.
     * @param symbolReferenceText Symbol reference in unparsed text format.
     */
    public SymbolReference(AstNode ownerNode, String symbolReferenceText)
    {
        this.ownerNode = ownerNode;
        this.symbolReferenceText = symbolReferenceText;
    }

    /**
     * Gets referenced package.
     *
     * @return Referenced package or null if the reference cannot be resolved.
     */
    public Package getReferencedPackage()
    {
        return referencedPackage;
    }

    /**
     * Gets referenced package symbol.
     *
     * @return Referenced package symbol or null if the reference is a package.
     */
    public PackageSymbol getReferencedPackageSymbol()
    {
        return referencedPackageSymbol;
    }

    /**
     * Gets referenced scope symbol.
     *
     * @return Referenced scope symbol or null if the reference is a package or a package symbol.
     */
    public ScopeSymbol getReferencedScopeSymbol()
    {
        return referencedScopeSymbol;
    }

    /**
     * Resolves the symbol reference.
     *
     * @param packageNameMap Map of all registered packages.
     * @param ownerPackage Zserio package in which the symbol reference is defined.
     * @param ownerType ZserioType which is owner of the symbol reference or null.
     */
    void resolve(Map<PackageName, Package> packageNameMap, Package ownerPackage, ZserioScopedType ownerType)
    {
        if (resolveCalled)
            return;

        resolveCalled = true;

        // resolve in same resolution order as in expressions
        final String[] ids = symbolReferenceText.split("\\" + SYMBOL_REFERENCE_SEPARATOR);
        if (!resolveScopeSymbol(ownerPackage, ownerType, ids))
        {
            if (!resolvePackageSymbol(ownerPackage, ids))
            {
                if (!resolvePackage(packageNameMap, ownerPackage, ids))
                {
                    throw new ParserException(ownerNode,
                            "Unresolved referenced symbol '" + symbolReferenceText + "'!");
                }
            }
        }
    }

    private boolean resolveScopeSymbol(Package ownerPackage, ZserioScopedType ownerType, String[] ids)
    {
        if (ids.length == 1 && ownerType != null)
        {
            // try to resolve it in owner type
            final FoundSymbol foundScopeSymbol = ownerType.getScope().findSymbol(ids[0]);
            if (foundScopeSymbol != null)
            {
                referencedPackage = ownerType.getPackage();
                referencedPackageSymbol = ownerType;
                referencedScopeSymbol = foundScopeSymbol.getSymbol();
                return true;
            }
        }
        else if (ids.length > 1)
        {
            final PackageName packageName = getPackageName(ownerPackage, ids, ids.length - 2);
            final PackageSymbol foundPackageSymbol =
                    ownerPackage.getVisibleSymbol(ownerNode, packageName, ids[ids.length - 2]);
            if (foundPackageSymbol instanceof ZserioScopedType)
            {
                final Scope scope = ((ZserioScopedType)foundPackageSymbol).getScope();
                final String scopeSymbolName = ids[ids.length - 1];
                final FoundSymbol foundScopeSymbol = scope.findSymbol(scopeSymbolName);
                if (foundScopeSymbol != null)
                {
                    referencedPackage = foundPackageSymbol.getPackage();
                    referencedPackageSymbol = foundPackageSymbol;
                    referencedScopeSymbol = foundScopeSymbol.getSymbol();
                    return true;
                }
                else
                {
                    throw new ParserException(ownerNode, "Unresolved referenced symbol '" +
                            scopeSymbolName + "' for type '" +
                            getPackageSymbolName(foundPackageSymbol) + "'!");
                }
            }
        }

        return false;
    }

    private boolean resolvePackageSymbol(Package ownerPackage, String[] ids)
    {
        if (ids.length != 0)
        {
            final PackageName packageName = getPackageName(ownerPackage, ids, ids.length - 1);
            final PackageSymbol foundPackageSymbol =
                    ownerPackage.getVisibleSymbol(ownerNode, packageName, ids[ids.length - 1]);
            if (foundPackageSymbol != null)
            {
                referencedPackage = foundPackageSymbol.getPackage();
                referencedPackageSymbol = foundPackageSymbol;
                return true;
            }
        }

        return false;
    }

    private boolean resolvePackage(Map<PackageName, Package> packageNameMap, Package ownerPackage, String[] ids)
    {
        final PackageName packageName = getPackageName(ownerPackage, ids, ids.length);
        final Package foundPackage = packageNameMap.get(packageName);
        if (foundPackage != null)
        {
            referencedPackage = foundPackage;
            return true;
        }

        return false;
    }

    private PackageName getPackageName(Package ownerPackage, String ids[], int numIds)
    {
        if (numIds < 1)
            return PackageName.EMPTY;

        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        packageNameBuilder.append(ownerPackage.getTopLevelPackageName());
        for (int i = 0; i < numIds; ++i)
            packageNameBuilder.addId(ids[i]);

        return packageNameBuilder.get();
    }

    private String getPackageSymbolName(PackageSymbol referencedPackageSymbol)
    {
        // return template name for template instantiations
        if (referencedPackageSymbol instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType template =
                    ((ZserioTemplatableType)referencedPackageSymbol).getTemplate();
            if (template != null)
                return template.getName();
        }

        return referencedPackageSymbol.getName();
    }

    private static final String SYMBOL_REFERENCE_SEPARATOR = ".";

    private final AstNode ownerNode;
    private final String symbolReferenceText;

    private boolean resolveCalled = false; // even unsuccessful resolve means that the symbol has been resolved
    private Package referencedPackage = null;
    private PackageSymbol referencedPackageSymbol = null;
    private ScopeSymbol referencedScopeSymbol = null;
}
