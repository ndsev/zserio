package zserio.ast;

/** Param tag documentation node used to document parameters. */
public class DocTagParam extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param paramName Parameter name.
     * @param firstLine First line of parameter description.
     */
    public DocTagParam(AstLocation location, String paramName, DocTextLine firstLine)
    {
        super(location, firstLine);

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