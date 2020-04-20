package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** The class implements validation of symbols within a single package. */
class PackageIdentifierValidator
{
    /**
     * Validates that type name satisfies requirements for safe generation.
     *
     * @param type Zserio type to validate.
     */
    public void validateTypeName(ZserioType type)
    {
        validateSymbol(type.getName(), type);
    }

    /**
     * Validates that the symbol name satisfies requirements for safe generation.
     *
     * @param name Symbol name to validate.
     * @param symbol AST node of the symbol for error reporting.
     */
    public void validateSymbol(String name, AstNode symbol)
    {
        final String lowerCaseName = name.toLowerCase(Locale.ENGLISH);
        final AstNode addedName = symbolsMap.put(lowerCaseName, symbol);
        if (addedName != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(symbol.getLocation(),
                    "Symbol '" + name + "' is not unique (case insensitive) within this package!");
            stackedException.pushMessage(addedName.getLocation(), "    Conflicting symbol defined here.");
            throw stackedException;
        }
    }

    private Map<String, AstNode> symbolsMap = new HashMap<String, AstNode>();
};
