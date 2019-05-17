package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * Location in AST.
 */
public class AstLocation
{
    /**
     * Constructor.
     *
     * @param token ANTLR4 token to localize AST node in the sources.
     */
    public AstLocation(Token token)
    {
        if (token == null)
        {
            fileName = null;
            line = 0;
            column = 0;
        }
        else
        {
            fileName = token.getInputStream().getSourceName();
            line = token.getLine();
            column = token.getCharPositionInLine() + 1;
        }
    }

    public AstLocation(String fileName, int line, int charPositionInLine)
    {
        this.fileName = fileName;
        this.line = line;
        this.column = charPositionInLine + 1;
    }

    /**
     * Gets file name where the AST node is localized.
     *
     * @return File name.
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Gets line number where the AST node is localized.
     *
     * @return Line number.
     */
    public int getLine()
    {
        return line;
    }

    /**
     * Gets column number where the AST node is localized.
     *
     * @return Column number.
     */
    public int getColumn()
    {
        return column;
    }

    private final String fileName;
    private final int line;
    private final int column;
}
