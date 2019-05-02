package zserio.ast4.doc;

import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

public class DocTextLine extends AstNodeBase
{
    public DocTextLine(Token token, List<DocText> docTexts)
    {
        super(token);

        this.docTexts = docTexts;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTextLine(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocText docText : docTexts)
            docText.accept(visitor);
    }

    public Iterable<DocText> getTexts()
    {
        return docTexts;
    }

    private final List<DocText> docTexts;
}
