package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class implements validation of symbols used within SQL extension.
 *
 * Since SQLite itself is case insensitive, we must ensure that all columns in SQL table and all tables
 * in SQL database are unique using case insensitive comparison.
 */
class SqlIdentifierValidator
{
    /**
     * Validates that the symbol name satisfies requirements for safe generation.
     * All symbols within a SQL entity must be unique using case insensitive comparison.
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
                    "Symbol '" + name + "' is not unique (case insensitive) within the SQL type!");
            stackedException.pushMessage(addedName.getLocation(), "    Conflicting symbol defined here");
            throw stackedException;
        }
    }

    private Map<String, AstNode> symbolsMap = new HashMap<String, AstNode>();
};
