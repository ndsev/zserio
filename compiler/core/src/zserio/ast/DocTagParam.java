package zserio.ast;

import org.antlr.v4.runtime.Token;

/** Param tag documentation node used to document parameters. */
public class DocTagParam extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param token     ANTLR4 token to localize AST node in the sources.
     * @param paramName Parameter name.
     * @param firstLine First line of parameter description.
     */
    public DocTagParam(Token token, String paramName, DocTextLine firstLine)
    {
        super(token, firstLine);

        this.paramName = paramName;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagParam(this);
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getParamName()
    {
        return paramName;
    }

    private final String paramName;
}