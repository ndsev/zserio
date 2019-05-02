package zserio.ast4.doc;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

abstract class DocMultilineNode extends AstNodeBase
{
    public DocMultilineNode(Token token, DocTextLine docTextLine)
    {
        super(token);

        docTextLines.add(docTextLine);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocTextLine docTextLine : docTextLines)
            docTextLine.accept(visitor);
    }

    public Iterable<DocTextLine> getTextLines()
    {
        return docTextLines;
    }

    protected void addLine(DocTextLine docTextLine)
    {
        docTextLines.add(docTextLine);
    }

    private final List<DocTextLine> docTextLines = new ArrayList<DocTextLine>();
};
