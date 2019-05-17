package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * Single documentation line AST node.
 */
public class DocLine extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      Location of this AST node.
     * @param lineElements  List of line elements which form the current documentation line.
     */
    public DocLine(AstLocation location, List<DocLineElement> lineElements)
    {
        super(location);

        this.lineElements = lineElements;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocLine(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocLineElement docLineElement : lineElements)
            docLineElement.accept(visitor);
    }

    /**
     * Gets documentation line elements forming the current documentation line.
     *
     * @return List of line elements.
     */
    public List<DocLineElement> getLineElements()
    {
        return Collections.unmodifiableList(lineElements);
    }

    private final List<DocLineElement> lineElements;
}
