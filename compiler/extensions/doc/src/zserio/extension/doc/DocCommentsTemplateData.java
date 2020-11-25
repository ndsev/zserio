package zserio.extension.doc;

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
import zserio.tools.ZserioToolPrinter;

/**
 * FreeMarker template data for documentation comments in the package used by Package emitter.
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

    public Iterable<DocCommentTemplateData> getCommentsList()
    {
        return commentsList;
    }

    public boolean getIsDeprecated()
    {
        return isDeprecated;
    }

    public static class DocCommentTemplateData
    {
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

        public DocCommentTemplateData(PackageTemplateDataContext context, DocCommentMarkdown docCommentMarkdown)
        {
            isDeprecated = false;

            final DocResourceManager docResourceManager = context.getDocResourceManager();
            markdownHtml = DocMarkdownToHtmlConverter.convert(docResourceManager,
                    docCommentMarkdown.getLocation(), docCommentMarkdown.getMarkdown());
        }

        public String getMarkdownHtml()
        {
            return markdownHtml;
        }

        public Iterable<DocParagraphData> getParagraphs()
        {
            return docParagraphs;
        }

        public boolean getIsDeprecated()
        {
            return isDeprecated;
        }

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

        public static class DocTagSeeData
        {
            public DocTagSeeData(PackageTemplateDataContext context, DocTagSee docTagSee)
            {
                seeSymbol = SymbolTemplateDataCreator.createData(context,  docTagSee);
            }

            public SymbolTemplateData getSeeSymbol()
            {
                return seeSymbol;
            }

            private final SymbolTemplateData seeSymbol;
        }

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
            ZserioToolPrinter.printWarning(docComment, "Unknown documentation format!");
            return null;
        }
    }

    private final List<DocCommentTemplateData> commentsList = new ArrayList<DocCommentTemplateData>();
    private final boolean isDeprecated;
}
