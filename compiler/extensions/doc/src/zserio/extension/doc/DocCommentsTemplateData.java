package zserio.extension.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import zserio.ast.PackageSymbol;
import zserio.ast.ScopeSymbol;
import zserio.ast.SymbolReference;
import zserio.ast.ZserioType;
import zserio.tools.ZserioToolPrinter;

/**
 * The documentation comments data used for FreeMarker template during documentation generation.
 */
public class DocCommentsTemplateData
{
    public DocCommentsTemplateData(PackageTemplateDataContext context, List<DocComment> docComments)
    {
        boolean isDeprecated = false;
        for (DocComment docComment : docComments)
        {
            final DocCommentTemplateData docCommentData = createDocCommentTemplateData(context, docComment);
            if (docCommentData != null)
            {
                isDeprecated |= docCommentData.getIsDeprecated();
                commentsList.add(docCommentData);
            }
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
         * Constructor from classic documentation comment.
         *
         * @param context Template data context.
         * @param docCommentClassic Classic documentation comment.
         */
        public DocCommentTemplateData(PackageTemplateDataContext context, DocCommentClassic docCommentClassic)
        {
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

        /**
         * Constructor from markdown documentation comment.
         *
         * @param context TemplateDataContext.
         * @param docCommentMarkdown Markdown documentation comment.
         */
        public DocCommentTemplateData(PackageTemplateDataContext context, DocCommentMarkdown docCommentMarkdown)
        {
            isDeprecated = false;

            final DocResourceManager docResourceManager = context.getDocResourceManager();
            final Path origCwd = docResourceManager.getCurrentSourceDir();
            docResourceManager.setCurrentSourceDir(
                    Paths.get(docCommentMarkdown.getLocation().getFileName()).getParent());
            markdownHtml = DocMarkdownToHtmlConverter.convert(docResourceManager,
                    docCommentMarkdown.getLocation(), docCommentMarkdown.getMarkdown());
            docResourceManager.setCurrentSourceDir(origCwd);
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
            public DocParagraphData(PackageTemplateDataContext context, DocParagraph docParagraph)
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
                public DocElementData(PackageTemplateDataContext context, DocElement docElement)
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
            public DocMultilineData(PackageTemplateDataContext context, DocMultiline docMultiline)
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
                DocLineElementData(PackageTemplateDataContext context, DocLineElement docLineElement)
                {
                    final DocText docText = docLineElement.getDocText();
                    docString = docText != null ?
                            DocClassicToHtmlConverter.convert(docText.getText()) : null;

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
            public DocTagSeeData(PackageTemplateDataContext context, DocTagSee docTagSee)
            {
                final SymbolReference linkSymbolReference = docTagSee.getLinkSymbolReference();
                final PackageSymbol referencedPackageSymbol = linkSymbolReference.getReferencedPackageSymbol();
                final ScopeSymbol referencedScopeSymbol = linkSymbolReference.getReferencedScopeSymbol();
                SymbolTemplateData symbolData;
                if (referencedPackageSymbol == null)
                {
                    // this can happen if see tag link is invalid
                    symbolData = new SymbolTemplateData("", "unknownLink", "Unknown link", null,
                            new ArrayList<SymbolTemplateData>());
                }
                else if (referencedScopeSymbol == null)
                {
                    symbolData = SymbolTemplateDataCreator.createData(context, referencedPackageSymbol);
                }
                else
                {
                    symbolData = SymbolTemplateDataCreator.createData(context,
                            (ZserioType)referencedPackageSymbol, referencedScopeSymbol);
                }

                final String alias = docTagSee.getLinkAlias();
                seeSymbol = new SymbolTemplateData(alias, symbolData);
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
            public DocTagParamData(PackageTemplateDataContext context, DocTagParam docTagParam)
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

    private DocCommentTemplateData createDocCommentTemplateData(PackageTemplateDataContext context,
            DocComment docComment)
    {
        if (docComment instanceof DocCommentMarkdown)
        {
            return new DocCommentTemplateData(context, (DocCommentMarkdown)docComment);
        }
        else if (docComment instanceof DocCommentClassic)
        {
            return new DocCommentTemplateData(context, (DocCommentClassic)docComment);
        }
        else
        {
            ZserioToolPrinter.printWarning(docComment, "Unkown documentation format!");
            return null;
        }
    }

    private final List<DocCommentTemplateData> commentsList = new ArrayList<DocCommentTemplateData>();
    private final boolean isDeprecated;
}
