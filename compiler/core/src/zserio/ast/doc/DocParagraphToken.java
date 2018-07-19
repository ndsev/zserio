package zserio.ast.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zserio.antlr.DocCommentParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * Implements AST token for type DOC_PARAGRAPH.
 */
public class DocParagraphToken extends DocTokenAST
{
    /**
     * Empty constructor.
     */
    public DocParagraphToken()
    {
        docParagraphTextList = new ArrayList<DocParagraphTokenText>();
        docTagSeeList = new ArrayList<DocTagSeeToken>();
        docTagParamList = new ArrayList<DocTagParamToken>();
        docTagTodoList = new ArrayList<DocTagTodoToken>();
        wasText = false;
    }

    /**
     * Gets the list of paragraph texts stored in paragraph.
     *
     * @return The list of paragraph texts stored in paragraph.
     */
    public Iterable<DocParagraphTokenText> getParagraphTextList()
    {
        return docParagraphTextList;
    }

    /**
     * Gets the list of stand-alone see tags stored in paragraph.
     *
     * @return The list of stand-alone see tags stored in paragraph.
     */
    public Iterable<DocTagSeeToken> getTagSeeList()
    {
        return docTagSeeList;
    }

    /**
     * Gets the list of param tags stored in paragraph.
     *
     * @return The list of param tags stored in paragraph.
     */
    public Iterable<DocTagParamToken> getTagParamList()
    {
        return docTagParamList;
    }

    /**
     * Gets the list of todo tags stored in paragraph.
     *
     * @return The list of todo tags stored in paragraph.
     */
    public Iterable<DocTagTodoToken> getTagTodoList()
    {
        return docTagTodoList;
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

    /**
     * Helper class to store list of texts and see tags stored in the one paragraph token.
     */
    public static class DocParagraphTokenText implements Serializable
    {
        /**
         * Constructor.
         *
         * @param text The paragraph text to construct from.
         */
        public DocParagraphTokenText(String text)
        {
            textList = new ArrayList<String>();
            textList.add(text);
            tagSeeList = new ArrayList<DocTagSeeToken>();
        }

        /**
         * Adds the paragraph text into the list.
         *
         * @param text Paragraph text to add.
         */
        public void addText(String text)
        {
            textList.add(text);
        }

        /**
         * Adds the tag see token into the list.
         *
         * @param tagSee Tag see token to add.
         */
        public void addTagSee(DocTagSeeToken tagSee)
        {
            tagSeeList.add(tagSee);
        }

        /**
         * Checks if list of tag see tokens is empty.
         *
         * @return Returns true if list of tag see tokens is empty.
         */
        public boolean isTagSeeListEmpty()
        {
            return tagSeeList.isEmpty();
        }

        /**
         * Gets the list of the paragraph texts.
         *
         * @return The list of the paragraph texts.
         */
        public Iterable<String> getTextList()
        {
            return textList;
        }

        /**
         * Gets the list of the tag see tokens.
         *
         * @return The list of the tag see tokens.
         */
        public Iterable<DocTagSeeToken> getTagSeeList()
        {
            return tagSeeList;
        }

        private static final long serialVersionUID = 1L;

        private final List<String>          textList;
        private final List<DocTagSeeToken>  tagSeeList;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case DocCommentParserTokenTypes.DOC_TEXT:
            final String text = child.getText();
            if (docParagraphTextList.isEmpty())
            {
                docParagraphTextList.add(new DocParagraphTokenText(text));
            }
            else
            {
                final DocParagraphTokenText last = docParagraphTextList.get(docParagraphTextList.size() - 1);
                if (last.isTagSeeListEmpty())
                    last.addText(text);
                else
                    docParagraphTextList.add(new DocParagraphTokenText(text));
            }
            wasText = true;
            break;

        case DocCommentParserTokenTypes.DOC_TAG_SEE:
            if (!(child instanceof DocTagSeeToken))
                return false;

            final DocTagSeeToken seeToken = (DocTagSeeToken) child;
            if (wasText)
            {
                final DocParagraphTokenText last = docParagraphTextList.get(docParagraphTextList.size() - 1);
                last.addTagSee(seeToken);
            }
            else
            {
                docTagSeeList.add(seeToken);
            }
            break;

        case DocCommentParserTokenTypes.DOC_TAG_PARAM:
            if (!(child instanceof DocTagParamToken))
                return false;
            docTagParamList.add((DocTagParamToken) child);
            wasText = false;
            break;

        case DocCommentParserTokenTypes.DOC_TAG_TODO:
            if (!(child instanceof DocTagTodoToken))
                return false;
            docTagTodoList.add((DocTagTodoToken) child);
            wasText = false;
            break;

        case DocCommentParserTokenTypes.DOC_TAG_DEPRECATED:
            isDeprecated = true;
            wasText = false;
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 1L;

    private List<DocParagraphTokenText> docParagraphTextList;
    private List<DocTagSeeToken>        docTagSeeList;
    private List<DocTagParamToken>      docTagParamList;
    private List<DocTagTodoToken>       docTagTodoList;
    private boolean                     isDeprecated;
    private boolean                     wasText;
}
