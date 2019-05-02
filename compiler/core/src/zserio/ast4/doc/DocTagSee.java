package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

public class DocTagSee extends AstNodeBase
{
    public DocTagSee(Token token, String alias, String id)
    {
        super(token);

        this.alias = alias;
        this.id = id;
    }

    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagSee(this);
    }

    public String getAlias()
    {
        return alias;
    }

    public String getId()
    {
        return id;
    }

    private final String alias;
    private final String id;
}