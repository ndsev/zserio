package zserio.ast4;

import org.antlr.v4.runtime.Token;

public class AstNodeLocation
{
    public AstNodeLocation(Token token)
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

    public String getFileName()
    {
        return fileName;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    private final String fileName;
    private final int line;
    private final int column;
}
