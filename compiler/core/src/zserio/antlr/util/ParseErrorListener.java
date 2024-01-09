package zserio.antlr.util;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import zserio.antlr.ZserioParser;
import zserio.ast.AstLocation;
import zserio.ast.ParserException;
import zserio.ast.ParserStackedException;

/**
 * ANTLR4 error listener implementation which terminates parsing in case of an parsing error.
 */
public final class ParseErrorListener extends BaseErrorListener
{
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
            int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException
    {
        final AstLocation location =
                new AstLocation(recognizer.getInputStream().getSourceName(), line, charPositionInLine);
        if (e instanceof InputMismatchException)
        {
            final Token offendingToken = e.getOffendingToken();
            if (offendingToken != null)
            {
                if (isKeyword(offendingToken))
                {
                    final ParserStackedException stackedException = new ParserStackedException(
                            location, "'" + offendingToken.getText() + "' is a reserved keyword!");
                    stackedException.pushMessage(location, msg);
                    throw stackedException;
                }

                if (isInvalidStringLiteral(offendingToken))
                    throw new ParserException(
                            location, "'" + offendingToken.getText() + "' is an invalid string literal!");

                if (isInvalidToken(offendingToken))
                    throw new ParserException(
                            location, "'" + offendingToken.getText() + "' is an invalid token!");
            }
        }

        throw new ParserException(location, msg);
    }

    private boolean isKeyword(Token token)
    {
        // according to keywords defined in ZserioLexer.g4
        return (token.getType() >= ZserioParser.ALIGN && token.getType() <= ZserioParser.VARUINT64);
    }

    private boolean isInvalidStringLiteral(Token token)
    {
        return token.getType() == ZserioParser.INVALID_STRING_LITERAL;
    }

    private boolean isInvalidToken(Token token)
    {
        return token.getType() == ZserioParser.INVALID_TOKEN;
    }
}
