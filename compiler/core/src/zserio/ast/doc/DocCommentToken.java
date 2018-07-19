package zserio.ast.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.DocCommentParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.ZserioType;

/**
 * Implements the root AST token for documentation comment.
 */
public class DocCommentToken extends DocTokenAST
{
    /**
     * Empty constructor.
     */
    public DocCommentToken()
    {
        paragraphList = new ArrayList<DocParagraphToken>();
    }

    /**
     * Gets list of documentation paragraph tokens.
     *
     * @return List of documentation paragraph tokens.
     */
    public Iterable<DocParagraphToken> getParagraphList()
    {
        return paragraphList;
    }

    /**
     * Gets deprecated flag.
     *
     * @return Returns true if documentation comment deprecated tag has been specified.
     */
    public boolean isDeprecated()
    {
        return isDeprecated;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case DocCommentParserTokenTypes.DOC_PARAGRAPH:
            if (!(child instanceof DocParagraphToken))
                return false;
            paragraphList.add((DocParagraphToken) child);
            break;

        default:
            return false;
        }

        return true;
    }

    @Override
    protected void check(ZserioType owner) throws ParserException
    {
        for (DocParagraphToken paragraph : paragraphList)
            if (paragraph.isDeprecated())
                isDeprecated = true;
    }

    private static final long serialVersionUID = 1L;

    private List<DocParagraphToken> paragraphList;
    private boolean                 isDeprecated;
}
