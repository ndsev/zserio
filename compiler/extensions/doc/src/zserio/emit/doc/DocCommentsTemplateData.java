package zserio.emit.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocComment;
import zserio.ast.DocCommentClassic;
import zserio.ast.DocCommentMarkdown;
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
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * The documentation comments data used for FreeMarker template during documentation generation.
 */
public class DocCommentsTemplateData
{
    public DocCommentsTemplateData(TemplateDataContext context, List<DocComment> docComments)
            throws ZserioEmitException
    {
        boolean isDeprecated = false;
        for (DocComment docComment : docComments)
        {
            final DocCommentTemplateData docCommentData = new DocCommentTemplateData(context, docComment);
            isDeprecated |= docCommentData.getIsDeprecated();
            commentsList.add(docCommentData);
        }
        this.isDeprecated = isDeprecated;
    }

    /**
     * Returns list of template data for particular documentation comments.
     *
     * @return List of documentation comments template data.
     */
    public Iterable<DocCommentTemplateData> getCommentsList()
    {
        return commentsList;
    }

    /**
     * Returns true if the documented element is deprecated.
     */
    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }

    public static class DocCommentTemplateData
    {
        /**
         * Constructor.
         *
         * @param context    Template data context.
         * @param docComment Documentation comment to construct from or null in case of no comment.
         *
         * @throws ZserioEmitException Throws in case of any internal error.
         */
        public DocCommentTemplateData(TemplateDataContext context, DocComment docComment)
                throws ZserioEmitException
        {
            if (docComment instanceof DocCommentMarkdown)
            {
                isDeprecated = false;
                final DocCommentMarkdown docCommentMarkdown = (DocCommentMarkdown)docComment;

                final ResourceManager resourceManager = context.getResourceManager();
                final Path origCwd = resourceManager.getCurrentSourceDir();
                resourceManager.setCurrentSourceDir(
                        Paths.get(docComment.getLocation().getFileName()).getParent());
                markdownHtml = MarkdownToHtmlConverter.convert(resourceManager,
                        docCommentMarkdown.getLocation(), docCommentMarkdown.getMarkdown());
                resourceManager.setCurrentSourceDir(origCwd);
            }
            else if (docComment instanceof DocCommentClassic)
            {
                final DocCommentClassic docCommentClassic = (DocCommentClassic)docComment;
                boolean isDeprecated = false;

                for (DocParagraph docParagraph : docCommentClassic.getParagraphs())
                {
                    docParagraphs.add(new DocParagraphData(context, docParagraph));

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
                this.markdownHtml = null;
            }
            else
            {
                throw new ZserioEmitException("Unknown documentation format!");
            }
        }

        /**
         * Returns the documentation HTML rendered from markdown.
         */
        public String getMarkdownHtml()
        {
            return markdownHtml;
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
            public DocParagraphData(TemplateDataContext context, DocParagraph docParagraph)
            {
                for (DocElement docElement : docParagraph.getDocElements())
                    docElements.add(new DocElementData(context, docElement));
            }

            public Iterable<DocElementData> getElements()
            {
                return docElements;
            }

            public static class DocElementData
            {
                public DocElementData(TemplateDataContext context, DocElement docElement)
                {
                    final DocMultiline multiline = docElement.getDocMultiline();
                    this.multiline = multiline != null ? new DocMultilineData(context, multiline) : null;

                    final DocTagSee seeTag = docElement.getSeeTag();
                    this.seeTag = seeTag != null ? new DocTagSeeData(context, seeTag) : null;

                    final DocTagTodo todoTag = docElement.getTodoTag();
                    this.todoTag = todoTag != null ? new DocMultilineData(context, todoTag) : null;

                    final DocTagParam paramTag = docElement.getParamTag();
                    this.paramTag = paramTag != null ? new DocTagParamData(context, paramTag) : null;

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
            public DocMultilineData(TemplateDataContext context, DocMultiline docMultiline)
            {
                for (DocLine docLine : docMultiline.getLines())
                {
                    for (DocLineElement docLineElement : docLine.getLineElements())
                    {
                        docLineElements.add(new DocLineElementData(context, docLineElement));
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
                DocLineElementData(TemplateDataContext context, DocLineElement docLineElement)
                {
                    final DocText docText = docLineElement.getDocText();
                    docString = docText != null ?
                            StringHtmlUtil.escapeCommentsForHtml(docText.getText()) : null;

                    final DocTagSee docTagSee = docLineElement.getSeeTag();
                    seeTag = docTagSee != null ? new DocTagSeeData(context, docTagSee) : null;
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
            public DocTagSeeData(TemplateDataContext context, DocTagSee docTagSee)
            {
                final SymbolReference linkSymbolReference = docTagSee.getLinkSymbolReference();
                final ZserioType referencedType = linkSymbolReference.getReferencedType();
                final AstNode referencedSymbol = linkSymbolReference.getReferencedSymbol();

                SymbolTemplateData symbolData = new SymbolTemplateData("", "unknownLink", "Unknown link", null);
                if (referencedType == null)
                {
                    if (referencedSymbol != null)
                        symbolData = SymbolTemplateDataCreator.createData(context, referencedSymbol);
                }
                else
                {
                    if (referencedSymbol != null)
                    {
                        symbolData = SymbolTemplateDataCreator.createData(context, referencedType,
                                referencedSymbol);
                    }
                    else if (linkSymbolReference.getReferencedSymbolName() == null)
                    {
                        symbolData = SymbolTemplateDataCreator.createData(context, referencedType);
                    }
                }

                final String alias = docTagSee.getLinkAlias();
                seeSymbol = new SymbolTemplateData(alias, symbolData.getHtmlClass(), symbolData.getHtmlTitle(),
                        symbolData.getHtmlLink());
            }

            public SymbolTemplateData getSeeSymbol()
            {
                return seeSymbol;
            }

            private final SymbolTemplateData seeSymbol;
        }

        /**
         * Helper class to model the documentation param tag used for FreeMarker template.
         */
        public static class DocTagParamData
        {
            public DocTagParamData(TemplateDataContext context, DocTagParam docTagParam)
            {
                name = docTagParam.getParamName();

                description = new DocMultilineData(context, docTagParam);
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
        private final String markdownHtml;
        private final boolean isDeprecated;
    }

    private final List<DocCommentTemplateData> commentsList = new ArrayList<DocCommentTemplateData>();
    private final boolean isDeprecated;
}
