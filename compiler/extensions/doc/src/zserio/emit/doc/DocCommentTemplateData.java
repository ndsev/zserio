package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.SymbolReference;
import zserio.ast.doc.DocCommentToken;
import zserio.ast.doc.DocParagraphToken;
import zserio.ast.doc.DocParagraphToken.DocParagraphTokenText;
import zserio.ast.doc.DocTagParamToken;
import zserio.ast.doc.DocTagSeeToken;
import zserio.ast.doc.DocTagTodoToken;

/**
 * The documentation comment data used for FreeMarker template during documentation generation.
 */
public class DocCommentTemplateData
{
    /**
     * Constructor.
     *
     * @param docComment Documentation comment token to construct from or null in case of no comment.
     */
    public DocCommentTemplateData(DocCommentToken docComment)
    {
        docParagraphList = new ArrayList<DocParagraph>();
        if (docComment != null)
        {
            for (DocParagraphToken docParagraphToken : docComment.getParagraphList())
                docParagraphList.add(new DocParagraph(docParagraphToken));

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
    public Iterable<DocParagraph> getParagraphList()
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
    public static class DocParagraph
    {
        public DocParagraph(DocParagraphToken docParagraphToken)
        {
            docParagraphTextList = new ArrayList<DocParagraphText>();
            for (DocParagraphTokenText paragraphText : docParagraphToken.getParagraphTextList())
                docParagraphTextList.add(new DocParagraphText(paragraphText));

            docTagTodoList = new ArrayList<DocTagTodo>();
            for (DocTagTodoToken tagTodoToken : docParagraphToken.getTagTodoList())
                docTagTodoList.add(new DocTagTodo(tagTodoToken));

            docTagSeeList = new ArrayList<DocTagSee>();
            for (DocTagSeeToken tagSeeToken : docParagraphToken.getTagSeeList())
                docTagSeeList.add(new DocTagSee(tagSeeToken));

            docTagParamList = new ArrayList<DocTagParam>();
            for (DocTagParamToken tagParamToken : docParagraphToken.getTagParamList())
                docTagParamList.add(new DocTagParam(tagParamToken));
        }

        public Iterable<DocParagraphText> getParagraphTextList()
        {
            return docParagraphTextList;
        }

        public Iterable<DocTagTodo> getTagTodoList()
        {
            return docTagTodoList;
        }

        public Iterable<DocTagSee> getTagSeeList()
        {
            return docTagSeeList;
        }

        public Iterable<DocTagParam> getTagParamList()
        {
            return docTagParamList;
        }

        /**
         * Helper class to model the documentation paragraph text used for FreeMarker template.
         */
        public static class DocParagraphText
        {
            public DocParagraphText(DocParagraphTokenText docParagraphToken)
            {
                textList = new ArrayList<String>();
                for (String paragraphText : docParagraphToken.getTextList())
                    textList.add(StringHtmlUtil.escapeCommentsForHtml(paragraphText));

                tagSeeList = new ArrayList<DocTagSee>();
                for (DocTagSeeToken tagSeeToken : docParagraphToken.getTagSeeList())
                    tagSeeList.add(new DocTagSee(tagSeeToken));
            }

            public Iterable<String> getTextList()
            {
                return textList;
            }

            public Iterable<DocTagSee> getTagSeeList()
            {
                return tagSeeList;
            }

            private final List<String>      textList;
            private final List<DocTagSee>   tagSeeList;
        }

        /**
         * Helper class to model the documentation see tag used for FreeMarker template.
         */
        public static class DocTagSee
        {
            public DocTagSee(DocTagSeeToken tagSeeToken)
            {
                final String linkAlias = tagSeeToken.getLinkAlias();
                alias = (linkAlias == null) ? "" : linkAlias;
                final SymbolReference linkSymbolReference = tagSeeToken.getLinkSymbolReference();
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
        public static class DocTagTodo
        {
            public DocTagTodo(DocTagTodoToken tagTodoToken)
            {
                textList = new ArrayList<String>();
                for (String todoText : tagTodoToken.getTodoTextList())
                    textList.add(StringHtmlUtil.escapeCommentsForHtml(todoText));
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
        public static class DocTagParam
        {
            public DocTagParam(DocTagParamToken tagParamToken)
            {
                name = tagParamToken.getParamName();

                descriptionList = new ArrayList<String>();
                for (String paramDescription : tagParamToken.getParamDescriptionList())
                    descriptionList.add(StringHtmlUtil.escapeCommentsForHtml(paramDescription));
            }

            public String getName()
            {
                return name;
            }

            public Iterable<String> getDescriptionList()
            {
                return descriptionList;
            }

            private final String        name;
            private final List<String>  descriptionList;
        }

        private final List<DocParagraphText>    docParagraphTextList;
        private final List<DocTagTodo>          docTagTodoList;
        private final List<DocTagSee>           docTagSeeList;
        private final List<DocTagParam>         docTagParamList;
    }

    private final List<DocParagraph>    docParagraphList;
    private final boolean               isDeprecated;
}
