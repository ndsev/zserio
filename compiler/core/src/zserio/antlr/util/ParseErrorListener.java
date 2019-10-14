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
public class ParseErrorListener extends BaseErrorListener
{
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
            int line, int charPositionInLine, String msg,
            RecognitionException e) throws ParseCancellationException
    {
        final AstLocation location = new AstLocation(recognizer.getInputStream().getSourceName(), line,
                charPositionInLine);
        if (e instanceof InputMismatchException && isKeyword(e.getOffendingToken()))
        {
            final ParserStackedException stackedException = new ParserStackedException(location,
                    "'" + e.getOffendingToken().getText() + "' is a reserved keyword!");
            stackedException.pushMessage(location,  msg);
            throw stackedException;
        }
        else
        {
            throw new ParserException(new AstLocation(recognizer.getInputStream().getSourceName(), line,
                    charPositionInLine), msg);
        }
    }

    private boolean isKeyword(Token token)
    {
        if (token == null)
            return false;

        // according to keywords defined in ZserioLexer.g4
        if (token.getType() >= ZserioParser.ALIGN && token.getType() <= ZserioParser.VARUINT64)
            return true;

        return false;
    }
}

