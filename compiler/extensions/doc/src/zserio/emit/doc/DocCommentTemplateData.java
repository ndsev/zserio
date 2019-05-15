package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.DocMultilineNode;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;
import zserio.ast.DocTextElement;
import zserio.ast.DocTextLine;
import zserio.ast.SymbolReference;
import zserio.emit.common.ZserioEmitException;

/**
 * The documentation comment data used for FreeMarker template during documentation generation.
 */
public class DocCommentTemplateData
{
    /**
     * Constructor.
     *
     * @param docComment Documentation comment to construct from or null in case of no comment.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public DocCommentTemplateData(DocComment docComment) throws ZserioEmitException
    {
        if (docComment != null)
        {
            for (DocParagraph docParagraph : docComment.getParagraphs())
            docParagraphList.add(new DocMultilineNodeData(docParagraph));

            for (DocTagTodo docTagTodo : docComment.getTodoTags())
                docTagTodoList.add(new DocTagTodoData(docTagTodo));

            for (DocTagSee docTagSee : docComment.getSeeTags())
                docTagSeeList.add(new DocTagSeeData(docTagSee));

            for (DocTagParam docTagParam : docComment.getParamTags())
                docTagParamList.add(new DocTagParamData(docTagParam));

            isDeprecated = docComment.isDeprecated();
        }
        else
        {
            isDeprecated = false;
        }
    }

    /**
     * Returns the documentation comment paragraph list.
     */
    public Iterable<DocMultilineNodeData> getParagraphList()
    {
        return docParagraphList;
    }

    /**
     * Returns the todo tag list.
     */
    public Iterable<DocTagTodoData> getTagTodoList()
    {
        return docTagTodoList;
    }

    /**
     * Returns the see tag list.
     */
    public Iterable<DocTagSeeData> getTagSeeList()
    {
        return docTagSeeList;
    }

    /**
     * Returns the param tag list.
     */
    public Iterable<DocTagParamData> getTagParamList()
    {
        return docTagParamList;
    }

    /**
     * Returns true if the documented element is deprecated.
     */
    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }

    /**
     * Helper class to model the documentation multiline text used for FreeMarker template.
     */
    public static class DocMultilineNodeData
    {
        public DocMultilineNodeData(DocMultilineNode docMultilineNode) throws ZserioEmitException
        {
            for (DocTextLine docTextLine : docMultilineNode.getTextLines())
            {
                for (DocText docText : docTextLine.getTexts())
                {
                    docTextList.add(new DocTextData(docText));
                }
            }
        }

        public Iterable<DocTextData> getDocTextList()
        {
            return docTextList;
        }

        public static class DocTextData
        {
            DocTextData(DocText docText) throws ZserioEmitException
            {
                final DocTextElement docTextElement = docText.getTextElement();
                docString = docTextElement != null ?
                        StringHtmlUtil.escapeCommentsForHtml(docTextElement.getText()) : null;

                final DocTagSee docTagSee = docText.getSeeTag();
                seeTag = docTagSee != null ? new DocTagSeeData(docTagSee) : null;
            }

            public String getDocString()
            {
                return docString;
            }

            public DocTagSeeData getSeeTag()
            {
                return seeTag;
            }

            private final String docString;
            private final DocTagSeeData seeTag;
        }

        private final List<DocTextData> docTextList = new ArrayList<DocTextData>();
    }

    /**
     * Helper class to model the documentation see tag used for FreeMarker template.
     */
    public static class DocTagSeeData
    {
        public DocTagSeeData(DocTagSee docTagSee) throws ZserioEmitException
        {
            alias = docTagSee.getLinkAlias();
            final SymbolReference linkSymbolReference = docTagSee.getLinkSymbolReference();
            url = DocEmitterTools.getUrlNameFromTypeAndFieldName(linkSymbolReference.getReferencedType(),
                    linkSymbolReference.getReferencedSymbolName());
        }

        public String getUrl()
        {
            return url;
        }

        public String getAlias()
        {
            return alias;
        }

        private final String alias;
        private final String url;
    }

    /**
     * Helper class to model the documentation todo tag used for FreeMarker template.
     */
    public static class DocTagTodoData
    {
        public DocTagTodoData(DocTagTodo docTagTodo)
        {
            textList = new ArrayList<String>();
            for (DocTextLine docTextLine : docTagTodo.getTextLines())
            {
                for (DocText docText : docTextLine.getTexts())
                {
                    final DocTextElement docTextElement = docText.getTextElement();
                    if (docTextElement != null)
                        textList.add(StringHtmlUtil.escapeCommentsForHtml(docTextElement.getText()));
                }
            }
        }

        public Iterable<String> getTextList()
        {
            return textList;
        }

        private final List<String> textList;
    }

    /**
     * Helper class to model the documentation param tag used for FreeMarker template.
     */
    public static class DocTagParamData
    {
        public DocTagParamData(DocTagParam docTagParam)
        {
            name = docTagParam.getParamName();

            descriptionList = new ArrayList<String>();
            for (DocTextLine docTextLine : docTagParam.getTextLines())
            {
                for (DocText docText : docTextLine.getTexts())
                {
                    final DocTextElement docTextElement = docText.getTextElement();
                    if (docTextElement != null)
                        descriptionList.add(StringHtmlUtil.escapeCommentsForHtml(docTextElement.getText()));
                }
            }
        }

        public String getName()
        {
            return name;
        }

        public Iterable<String> getDescriptionList()
        {
            return descriptionList;
        }

        private final String name;
        private final List<String> descriptionList;
    }

    private final List<DocMultilineNodeData> docParagraphList = new ArrayList<DocMultilineNodeData>();
    private final List<DocTagSeeData> docTagSeeList = new ArrayList<DocTagSeeData>();
    private final List<DocTagTodoData> docTagTodoList = new ArrayList<DocTagTodoData>();
    private final List<DocTagParamData> docTagParamList = new ArrayList<DocTagParamData>();
    private final boolean isDeprecated;
}
