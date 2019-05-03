package zserio.ast4;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * Single documentation line AST node.
 */
public class DocTextLine extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token    ANTLR4 token to localize AST node in the sources.
     * @param docTexts List of documentation texts which form the current documentation line.
     */
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
