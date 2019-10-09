package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.tools.StringJoinUtil;

/**
 * Location in AST.
 */
public class AstLocation
{
    /**
     * Constructor from grammar token.
     *
     * @param token Grammar token to localize AST node in the sources.
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

    /**
     * Constructor from file name and position.
     *
     * @param fileName           File name of the source.
     * @param line               Line number of the position in the source file.
     * @param charPositionInLine Character position in the source line.
     */
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

    @Override
    public String toString()
    {
        return fileName == null ? "" : StringJoinUtil.joinStrings(fileName, Integer.toString(line),
                Integer.toString(column), LOCATION_SEPARATOR);
    }

    private final String fileName;
    private final int line;
    private final int column;

    private static final String LOCATION_SEPARATOR = ":";
}
