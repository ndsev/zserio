package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * Single documentation line AST node.
 */
public class DocTextLine extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param docTexts List of documentation texts which form the current documentation line.
     */
    public DocTextLine(AstLocation location, List<DocText> docTexts)
    {
        super(location);

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

    /**
     * Gets documentation texts forming the current documentation line.
     *
     * @return List of doc texts.
     */
    public List<DocText> getTexts()
    {
        return Collections.unmodifiableList(docTexts);
    }

    private final List<DocText> docTexts;
}
