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
        docParagraphList = new ArrayList<DocParagraphData>();
        if (docComment != null)
        {
            docParagraphList.add(new DocParagraphData(docComment));
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
    public Iterable<DocParagraphData> getParagraphList()
    {
        return docParagraphList;
    }

    /**
     * Returns true if the documented element is deprecated.
     */
    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }

    /**
     * Helper class to model the documentation comment paragraph used for FreeMarker template.
     */
    public static class DocParagraphData
    {
        public DocParagraphData(DocComment docComment) throws ZserioEmitException
        {
            docParagraphTextList = new ArrayList<DocMultilineNodeData>();
            for (DocParagraph docParagraph : docComment.getParagraphs())
                docParagraphTextList.add(new DocMultilineNodeData(docParagraph));

            docTagTodoList = new ArrayList<DocTagTodoData>();
            for (DocTagTodo docTagTodo : docComment.getTodoTags())
                docTagTodoList.add(new DocTagTodoData(docTagTodo));

            docTagSeeList = new ArrayList<DocTagSeeData>();
            for (DocTagSee docTagSee : docComment.getSeeTags())
                docTagSeeList.add(new DocTagSeeData(docTagSee));

            docTagParamList = new ArrayList<DocTagParamData>();
            for (DocTagParam docTagParam : docComment.getParamTags())
                docTagParamList.add(new DocTagParamData(docTagParam));
        }

        public Iterable<DocMultilineNodeData> getParagraphTextList()
        {
            return docParagraphTextList;
        }

        public Iterable<DocTagTodoData> getTagTodoList()
        {
            return docTagTodoList;
        }

        public Iterable<DocTagSeeData> getTagSeeList()
        {
            return docTagSeeList;
        }

        public Iterable<DocTagParamData> getTagParamList()
        {
            return docTagParamList;
        }

        /**
         * Helper class to model the documentation paragraph text used for FreeMarker template.
         */
        public static class DocMultilineNodeData
        {
            public DocMultilineNodeData(DocMultilineNode docMultilineNode) throws ZserioEmitException
            {
                textList = new ArrayList<String>();
                tagSeeList = new ArrayList<DocTagSeeData>();

                for (DocTextLine docTextLine : docMultilineNode.getTextLines())
                {
                    for (DocText docText : docTextLine.getTexts())
                    {
                        final DocTextElement docTextElement = docText.getTextElement();
                        if (docTextElement != null)
                            textList.add(StringHtmlUtil.escapeCommentsForHtml(docTextElement.getText()));

                        final DocTagSee docTagSee = docText.getSeeTag();
                        if (docTagSee != null)
                            tagSeeList.add(new DocTagSeeData(docTagSee));
                    }
                }
            }

            public Iterable<String> getTextList()
            {
                return textList;
            }

            public Iterable<DocTagSeeData> getTagSeeList()
            {
                return tagSeeList;
            }

            private final List<String> textList;
            private final List<DocTagSeeData> tagSeeList;
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

        private final List<DocMultilineNodeData> docParagraphTextList;
        private final List<DocTagSeeData> docTagSeeList;
        private final List<DocTagTodoData> docTagTodoList;
        private final List<DocTagParamData> docTagParamList;
    }

    private final List<DocParagraphData> docParagraphList;
    private final boolean isDeprecated;
}
