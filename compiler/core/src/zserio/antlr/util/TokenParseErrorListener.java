package zserio.antlr.util;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import zserio.ast.AstLocation;

/**
 * ANTLR4 error listener implementation which terminates parsing in case of an parsing error.
 */
public class TokenParseErrorListener extends BaseErrorListener
{
    /**
     * Constructor from ANTLR4 token, expects that the input is parsed from a token
     * (e.g. documentation comment).
     *
     * @param token Token which text content is being parsed.
     */
    public TokenParseErrorListener(Token token)
    {
        sourceName = token.getInputStream().getSourceName();
        line = token.getLine();
        charPositionInLine = token.getCharPositionInLine();
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
            int line, int charPositionInLine, String msg,
            RecognitionException e) throws ParseCancellationException
    {
        throw new ParserException(new AstLocation(this.sourceName,
                this.line + line - 1, // lines are numbered from 1
                (line == 1 ? this.charPositionInLine + charPositionInLine : charPositionInLine)), msg);
    }

    final private String sourceName;
    final private int line;
    final private int charPositionInLine;
};
