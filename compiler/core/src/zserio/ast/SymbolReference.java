package zserio.ast;

import java.io.Serializable;

import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.ast.Package;

/**
 * The class represents symbol reference.
 *
 * Symbol reference can be for example
 *
 * - type reference ('packageName.FooConstant')
 * - reference to enumeration item ('packageName.FooEnumeration.BAR_ENUM_ITEM')
 * - reference to compound field ('packageName.FooCompoundType.field')
 *
 * Symbol reference is not AST node because it is used by unparsed texts in the following two situations:
 *
 * - documentation comment tag see token (@see)
 * - SQLite constraints (@expression)
 */
public class SymbolReference implements Serializable
{
    /**
     * Constructor from owner token and text.
     *
     * @param ownerToken          AST token which owns the symbol reference test.
     * @param symbolReferenceText Symbol reference in unparsed text format.
     */
    public SymbolReference(BaseTokenAST ownerToken, String symbolReferenceText)
    {
        this.ownerToken = ownerToken;

        final String[] referenceElementList = symbolReferenceText.split("\\" + SYMBOL_REFERENCE_SEPARATOR);
        for (String referenceElement : referenceElementList)
        {
            if (referencedTypeName != null)
                referencedPackageName.addId(referencedTypeName);
            referencedTypeName = referenceElement;
        }
    }

    /**
     * Gets referenced type.
     *
     * @return Referenced type.
     */
    public ZserioType getReferencedType()
    {
        return referencedType;
    }

    /**
     * Gets referenced symbol.
     *
     * @return Referenced enumeration item or compound field or null if this is a type reference.
     */
    public Object getReferencedSymbol()
    {
        return referencedSymbol;
    }

    /**
     * Gets referenced symbol name.
     *
     * @return Referenced enumeration item or compound field in string format or null if this is a type
     *         reference.
     */
    public String getReferencedSymbolName()
    {
        return referencedSymbolName;
    }

    /**
     * Checks the correctness of the symbol reference.
     *
     * @param ownerType ZserioType which is owner of the symbol reference.
     *
     * @throws ParserException Throws if the symbol reference is invalid.
     */
    public void check(ZserioType ownerType) throws ParserException
    {
        // try if the last link component was a type name
        final Package ownerPackage = ownerType.getPackage();
        referencedType = ownerPackage.getVisibleType(ownerToken, referencedPackageName, referencedTypeName);
        if (referencedType == null)
        {
            // try if the last link component was not type name (can be a field name or enumeration item)
            referencedSymbolName = referencedTypeName;
            referencedTypeName = referencedPackageName.removeLastId();
            if (referencedTypeName == null)
            {
                // there is only symbol name, try to resolve it in owner scope
                referencedType = ownerType;
            }
            else
            {
                referencedType = ownerPackage.getVisibleType(ownerToken, referencedPackageName,
                        referencedTypeName);

                // this was our last attempt to resolve symbol type
                if (referencedType == null)
                    throw new ParserException(ownerToken, "Unresolved referenced symbol '" +
                            referencedSymbolName + "'!");
            }

            resolveSymbol(referencedSymbolName);
        }
    }

    private void resolveSymbol(String referencedSymbolName) throws ParserException
    {
        if (!(referencedType instanceof ZserioScopedType))
            throw new ParserException(ownerToken, "Referenced symbol type '" + referencedType.getName() +
                    "' can't refer to '" + referencedSymbolName + "'!");

        final Scope referencedScope = ((ZserioScopedType)referencedType).getScope();
        referencedSymbol = referencedScope.getSymbol(referencedSymbolName);
        if (referencedSymbol == null)
            throw new ParserException(ownerToken, "Unresolved referenced symbol '" + referencedSymbolName +
                    "' for type '" + referencedType.getName() + "'!");
    }

    private static final long serialVersionUID = 1L;

    private static final String SYMBOL_REFERENCE_SEPARATOR = ".";

    private final BaseTokenAST ownerToken;
    private final PackageName referencedPackageName = new PackageName();
    private String referencedTypeName = null;
    private String referencedSymbolName = null;
    private ZserioType referencedType = null;
    private Object referencedSymbol = null;
}
