package zserio.ast;

import zserio.ast.Scope.FoundSymbol;

/**
 * The class represents symbol reference.
 *
 * Symbol reference can be for example
 *
 * - reference to constant ('packageName.FooConstant')
 * - reference to enumeration item ('packageName.FooEnumeration.BAR_ENUM_ITEM')
 * - reference to compound field ('packageName.FooCompoundType.field')
 *
 * Symbol reference is not AST node because it is used by unparsed texts in the following situation:
 *
 * - documentation comment tag see token (@see)
 */
public class SymbolReference
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
     * Gets referenced package symbol.
     *
     * @return Referenced package symbol.
     */
    public PackageSymbol getReferencedPackageSymbol()
    {
        return referencedPackageSymbol;
    }

    /**
     * Gets referenced scope symbol.
     *
     * @return Referenced scope symbol or null if the reference is a package symbol.
     */
    public ScopeSymbol getReferencedScopeSymbol()
    {
        return referencedScopeSymbol;
    }

    /**
     * Resolves the symbol reference.
     *
     * @param ownerPackage Zserio package in which the symbol reference is defined.
     * @param ownerType ZserioType which is owner of the symbol reference or null.
     */
    void resolve(Package ownerPackage, ZserioScopedType ownerType)
    {
        if (resolveCalled)
            return;

        resolveCalled = true;

        // try if the last link component was a package symbol
        final PackageName.Builder referencedPackageNameBuilder = new PackageName.Builder();
        String referencedName = getReferencedName(referencedPackageNameBuilder);
        PackageName referencedPackageName = getReferencedPackageName(ownerPackage,
                referencedPackageNameBuilder);
        referencedPackageSymbol = ownerPackage.getVisibleSymbol(ownerNode, referencedPackageName,
                referencedName);
        if (referencedPackageSymbol == null)
        {
            // try if the last link component was not a package symbol (can be a field name or enumeration item)
            final String symbolName = ZserioTypeUtil.getFullName(referencedPackageName, referencedName);
            final String referencedScopeSymbolName = referencedName;
            referencedName = referencedPackageNameBuilder.removeLastId();
            if (referencedName == null)
            {
                // there is only symbol name, try to resolve it in owner scope
                referencedPackageSymbol = ownerType;
            }
            else
            {
                // try to resolve it again
                referencedPackageName = getReferencedPackageName(ownerPackage, referencedPackageNameBuilder);
                referencedPackageSymbol = ownerPackage.getVisibleSymbol(ownerNode, referencedPackageName,
                        referencedName);
            }

            if (!(referencedPackageSymbol instanceof ZserioScopedType))
                throw new ParserException(ownerNode, "Unresolved referenced symbol '" + symbolName + "'!");

            final Scope referencedScope = ((ZserioScopedType)referencedPackageSymbol).getScope();
            final FoundSymbol foundSymbol = referencedScope.findSymbol(referencedScopeSymbolName);
            if (foundSymbol == null)
            {
                throw new ParserException(ownerNode, "Unresolved referenced symbol '" +
                        referencedScopeSymbolName + "' for type '" +
                        getUnresolvedSymbolName(referencedPackageSymbol) + "'!");
            }
            referencedScopeSymbol = foundSymbol.getSymbol();
        }
    }

    private PackageName getReferencedPackageName(Package ownerPackage,
            PackageName.Builder referencedPackageNameBuilder)
    {
        final PackageName referencedPackageName = referencedPackageNameBuilder.get();
        if (referencedPackageName.isEmpty())
            return referencedPackageName;

        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        packageNameBuilder.append(ownerPackage.getTopLevelPackageName());
        packageNameBuilder.append(referencedPackageName);

        return packageNameBuilder.get();
    }

    private String getReferencedName(PackageName.Builder referencedPackageNameBuilder)
    {
        String referencedName = null;
        final String[] referenceElementList = symbolReferenceText.split("\\" + SYMBOL_REFERENCE_SEPARATOR);
        for (String referenceElement : referenceElementList)
        {
            if (referencedName != null)
                referencedPackageNameBuilder.addId(referencedName);
            referencedName = referenceElement;
        }
        return referencedName;
    }

    private String getUnresolvedSymbolName(PackageSymbol referencedPackageSymbol)
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
    private PackageSymbol referencedPackageSymbol = null;
    private ScopeSymbol referencedScopeSymbol = null;
}
