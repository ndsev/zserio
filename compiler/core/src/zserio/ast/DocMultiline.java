package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Documentation comment node which can have multiple documentation lines.
 */
public class DocMultiline extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      Location of this AST node.
     * @param docTextLine   First documentation line.
     */
    public DocMultiline(AstLocation location, DocLine docTextLine)
    {
        super(location);

        docLines.add(docTextLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocMultiline(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocLine docTextLine : docLines)
            docTextLine.accept(visitor);
    }

    /**
     * Gets documentation lines.
     *
     * @return List of documentation lines.
     */
    public List<DocLine> getLines()
    {
        return Collections.unmodifiableList(docLines);
    }

    /**
     * Adds the documentation text line.
     *
     * @param docLine Documentation line to add.
     */
    void addLine(DocLine docLine)
    {
        docLines.add(docLine);
    }

    private final List<DocLine> docLines = new ArrayList<DocLine>();
};
