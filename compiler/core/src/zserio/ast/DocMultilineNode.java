package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Documentation comment node which can have multiple documentation lines.
 */
public abstract class DocMultilineNode extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param docTextLine   First documentation line.
     */
    public DocMultilineNode(AstLocation location, DocTextLine docTextLine)
    {
        super(location);

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
