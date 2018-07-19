package zserio.antlr.util;

import antlr.RecognitionException;

/**
 * Unchecked exception which is thrown if any error during parsing or checking occurs.
 */
public class ParserException extends RecognitionException
{
    /**
     * Constructor from AST token and text.
     *
     * @param originToken The AST token which throws this exception.
     * @param message     The message describing exception.
     */
    public ParserException(BaseTokenAST originToken, String message)
    {
        this(originToken.getFileName(), originToken.getLine(), originToken.getColumn(), message);
    }

    /**
     * Constructor from file name and text.
     *
     * @param fileName The name of source file where the exception occurred.
     * @param message  The message describing exception.
     */
    public ParserException(String fileName, String message)
    {
        this(fileName, 0, 0, message);
    }

    /**
     * Constructor from file name, line, column and text.
     *
     * @param fileName The name of source file where the exception occurred.
     * @param line     The line number in source file where the exception occurred.
     * @param line     The column number in source file where the exception occurred.
     * @param message  The message describing exception.
     */
    public ParserException(String fileName, int line, int column, String message)
    {
        super(message, fileName, line, column);
    }

    private static final long serialVersionUID = 1L;
}
