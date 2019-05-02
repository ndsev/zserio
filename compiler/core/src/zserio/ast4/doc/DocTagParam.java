package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.ZserioAstVisitor;

public class DocTagParam extends DocMultilineNode
{
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

    public String getParamName()
    {
        return paramName;
    }

    private final String paramName;
}