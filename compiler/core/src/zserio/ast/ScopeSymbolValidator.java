package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class implements validation of local symbol names in a ZserioType.
 */
final class ScopeSymbolValidator
{
    /**
     * Checks that the symbol name satisfies the requirements for safe generation.
     *
     * @param name Symbol name to validate.
     * @param symbol AST node of the symbol for error reporting.
     */
    public void validate(String name, AstNode symbol)
    {
        final String normalizedName = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        final AstNode prevSymbol = symbolsMap.put(normalizedName, symbol);
        if (prevSymbol != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    symbol.getLocation(), "Symbol '" + name + "' differs only in a case of its first letter!");
            stackedException.pushMessage(prevSymbol.getLocation(), "    Conflicting symbol defined here");
            throw stackedException;
        }
    }

    private Map<String, AstNode> symbolsMap = new HashMap<String, AstNode>();
};
