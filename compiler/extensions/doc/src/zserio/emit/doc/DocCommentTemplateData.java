package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.DocElement;
import zserio.ast.DocLine;
import zserio.ast.DocLineElement;
import zserio.ast.DocMultiline;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;
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
            boolean isDeprecated = false;

            for (DocParagraph docParagraph : docComment.getParagraphs())
            {
                docParagraphs.add(new DocParagraphData(docParagraph));

                if (!isDeprecated)
                {
                    for (DocElement element : docParagraph.getDocElements())
                    {
                        if (element.getDeprecatedTag() != null)
                        {
                            isDeprecated = true;
                            break;
                        }
                    }
                }
            }

            this.isDeprecated = isDeprecated;
        }
        else
        {
            isDeprecated = false;
        }
    }

    /**
     * Returns the documentation comment paragraphs.
     */
    public Iterable<DocParagraphData> getParagraphs()
    {
        return docParagraphs;
    }

    /**
     * Returns true if the documented element is deprecated.
     */
    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }

    /**
     * Helper class to model the documentation paragraph used for FreeMarker template.
     */
    public static class DocParagraphData
    {
        public DocParagraphData(DocParagraph docParagraph) throws ZserioEmitException
        {
            for (DocElement docElement : docParagraph.getDocElements())
                docElements.add(new DocElementData(docElement));
        }

        public List<DocElementData> getElements()
        {
            return docElements;
        }

        public static class DocElementData
        {
            public DocElementData(DocElement docElement) throws ZserioEmitException
            {
                final DocMultiline multiline = docElement.getDocMultiline();
                this.multiline = multiline != null ? new DocMultilineData(multiline) : null;

                final DocTagSee seeTag = docElement.getSeeTag();
                this.seeTag = seeTag != null ? new DocTagSeeData(seeTag) : null;

                final DocTagTodo todoTag = docElement.getTodoTag();
                this.todoTag = todoTag != null ? new DocMultilineData(todoTag) : null;

                final DocTagParam paramTag = docElement.getParamTag();
                this.paramTag = paramTag != null ? new DocTagParamData(paramTag) : null;

                // deprecated tag is ignored here, solved in DocCommentTempateData
            }

            public DocMultilineData getMultiline()
            {
                return multiline;
            }

            public DocTagSeeData getSeeTag()
            {
                return seeTag;
            }

            public DocMultilineData getTodoTag()
            {
                return todoTag;
            }

            public DocTagParamData getParamTag()
            {
                return paramTag;
            }

            private final DocMultilineData multiline;
            private final DocTagSeeData seeTag;
            private final DocMultilineData todoTag;
            private final DocTagParamData paramTag;
        }

        private final List<DocElementData> docElements = new ArrayList<DocElementData>();
    }

    /**
     * Helper class to model the documentation multiline text used for FreeMarker template.
     */
    public static class DocMultilineData
    {
        public DocMultilineData(DocMultiline docMultiline) throws ZserioEmitException
        {
            for (DocLine docLine : docMultiline.getLines())
            {
                for (DocLineElement docLineElement : docLine.getLineElements())
                {
                    docLineElements.add(new DocLineElementData(docLineElement));
                }
            }
        }

        public Iterable<DocLineElementData> getDocLineElements()
        {
            return docLineElements;
        }

        /**
         * Helper class to model the documentation line element used for FreeMarker template.
         */
        public static class DocLineElementData
        {
            DocLineElementData(DocLineElement docLineElement) throws ZserioEmitException
            {
                final DocText docText = docLineElement.getDocText();
                docString = docText != null ?
                        StringHtmlUtil.escapeCommentsForHtml(docText.getText()) : null;

                final DocTagSee docTagSee = docLineElement.getSeeTag();
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

        private final List<DocLineElementData> docLineElements = new ArrayList<DocLineElementData>();
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
     * Helper class to model the documentation param tag used for FreeMarker template.
     */
    public static class DocTagParamData
    {
        public DocTagParamData(DocTagParam docTagParam) throws ZserioEmitException
        {
            name = docTagParam.getParamName();

            description = new DocMultilineData(docTagParam);
        }

        public String getName()
        {
            return name;
        }

        public DocMultilineData getDescription()
        {
            return description;
        }

        private final String name;
        private final DocMultilineData description;
    }

    private final List<DocParagraphData> docParagraphs = new ArrayList<DocParagraphData>();
    private final boolean isDeprecated;
}
