package zserio.ast4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * Documentation comment node which can have multiple documentation lines.
 */
public abstract class DocMultilineNode extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token         ANTLR4 token to localize AST node in the sources.
     * @param docTextLine   First documentation line.
     */
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

    /**
     * Gets documentation lines.
     *
     * @return List of documentation lines.
     */
    public List<DocTextLine> getTextLines()
    {
        return Collections.unmodifiableList(docTextLines);
    }

    protected void addLine(DocTextLine docTextLine)
    {
        docTextLines.add(docTextLine);
    }

    private final List<DocTextLine> docTextLines = new ArrayList<DocTextLine>();
};
