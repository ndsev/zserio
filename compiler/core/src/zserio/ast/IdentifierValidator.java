package zserio.ast;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioLexer;

/**
 * The class implements validation of zserio language identifiers.
 */
public final class IdentifierValidator
{
    /**
     * Checks that the language identifier satisfies the requirements for safe generation.
     *
     * @param id Zserio language identifier to validate.
     */
    public static void validate(String id)
    {
        if (id.toLowerCase(Locale.ENGLISH).startsWith("zserio"))
        {
            throw new RuntimeException(
                    "ZSERIO (case insensitive) is a reserved prefix and cannot be used in identifiers!");
        }
    }

    /**
     * Checks that the top level package identifier satisfies the requirements for safe generation.
     *
     * @param id Top level package identifier given by command line to validate.
     */
    public static void validateTopLevelPackageId(String id)
    {
        final CharStream input = CharStreams.fromString(id);
        final ZserioLexer lexer = new ZserioLexer(input);
        final Token token = lexer.nextToken();
        if (token == null || token.getType() != ZserioLexer.ID)
        {
            throw new RuntimeException("'" + id +
                    "' cannot begin with number and can contain only letters, underscore or numbers!");
        }

        validate(id);
    }
};
