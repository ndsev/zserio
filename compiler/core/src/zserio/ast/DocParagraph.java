package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Documentation paragraph.
 */
public class DocParagraph extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     */
    public DocParagraph(AstLocation location)
    {
        super(location);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocParagraph(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocElement docElement : docElements)
            docElement.accept(visitor);
    }

    public List<DocElement> getDocElements()
    {
        return Collections.unmodifiableList(docElements);
    }

    void addDocElement(DocElement docElement)
    {
        docElements.add(docElement);
    }

    private final List<DocElement> docElements = new ArrayList<DocElement>();
}
