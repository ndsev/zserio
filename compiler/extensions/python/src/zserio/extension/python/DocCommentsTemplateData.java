package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import zserio.ast.DocComment;
import zserio.ast.DocCommentClassic;
import zserio.ast.DocElement;
import zserio.ast.DocLine;
import zserio.ast.DocLineElement;
import zserio.ast.DocMultiline;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagDeprecated;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;

/**
 * FreeMarker template data for documentation comments.
 */
public class DocCommentsTemplateData
{
    public DocCommentsTemplateData(List<DocComment> docComments)
    {
        int stickyCommentsIndex = docComments.size();
        final ListIterator<DocComment> iterator = docComments.listIterator(docComments.size());
        while (iterator.hasPrevious() && iterator.previous().isSticky())
            --stickyCommentsIndex;

        for (int i = 0; i < docComments.size(); ++i)
        {
            if (i >= stickyCommentsIndex)
            {
                final DocComment docComment = docComments.get(i);
                comments.add(new DocCommentTemplateData(docComment.toClassic()));
            }
        }
    }

    public Iterable<DocCommentTemplateData> getComments()
    {
        return comments;
    }

    public static class DocCommentTemplateData
    {
        public DocCommentTemplateData(DocCommentClassic docCommentClassic)
        {
            for (DocParagraph docParagraph : docCommentClassic.getParagraphs())
                docParagraphs.add(new DocParagraphData(docParagraph));

            isOneLiner = docCommentClassic.isOneLiner();
        }

        public Iterable<DocParagraphData> getParagraphs()
        {
            return docParagraphs;
        }

        public boolean getIsOneLiner()
        {
            return isOneLiner;
        }

        public static class DocParagraphData
        {
            public DocParagraphData(DocParagraph docParagraph)
            {
                for (DocElement docElement : docParagraph.getDocElements())
                    docElements.add(new DocElementData(docElement));
            }

            public Iterable<DocElementData> getElements()
            {
                return docElements;
            }

            public static class DocElementData
            {
                public DocElementData(DocElement docElement)
                {
                    final DocMultiline multiline = docElement.getDocMultiline();
                    this.multiline = (multiline != null) ? new DocMultilineData(multiline) : null;

                    final DocTagSee seeTag = docElement.getSeeTag();
                    this.seeTag = (seeTag != null) ? new DocTagSeeData(seeTag) : null;

                    final DocTagTodo todoTag = docElement.getTodoTag();
                    this.todoTag = (todoTag != null) ? new DocMultilineData(todoTag) : null;

                    final DocTagParam paramTag = docElement.getParamTag();
                    this.paramTag = (paramTag != null) ? new DocTagParamData(paramTag) : null;

                    final DocTagDeprecated deprecatedTag = docElement.getDeprecatedTag();
                    this.isDeprecated = deprecatedTag != null;
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

                public boolean getIsDeprecated()
                {
                    return isDeprecated;
                }

                private final DocMultilineData multiline;
                private final DocTagSeeData seeTag;
                private final DocMultilineData todoTag;
                private final DocTagParamData paramTag;
                private final boolean isDeprecated;
            }

            private final List<DocElementData> docElements = new ArrayList<DocElementData>();
        }

        public static class DocMultilineData
        {
            public DocMultilineData(DocMultiline docMultiline)
            {
                for (DocLine docLine : docMultiline.getLines())
                    lines.add(new DocLineData(docLine));
            }

            public Iterable<DocLineData> getLines()
            {
                return lines;
            }

            public static class DocLineData
            {
                public DocLineData(DocLine docLine)
                {
                    for (DocLineElement docLineElement : docLine.getLineElements())
                    {
                        lineElements.add(new DocLineElementData(docLineElement));
                    }
                }

                public Iterable<DocLineElementData> getLineElements()
                {
                    return lineElements;
                }

                private final List<DocLineElementData> lineElements = new ArrayList<DocLineElementData>();
            }

            public static class DocLineElementData
            {
                public DocLineElementData(DocLineElement docLineElement)
                {
                    final DocText docText = docLineElement.getDocText();
                    docString = (docText != null) ? docText.getText() : null;

                    final DocTagSee docTagSee = docLineElement.getSeeTag();
                    seeTag = (docTagSee != null) ? new DocTagSeeData(docTagSee) : null;
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

            private final List<DocLineData> lines = new ArrayList<DocLineData>();
        }

        public static class DocTagSeeData
        {
            public DocTagSeeData(DocTagSee docTagSee)
            {
                alias = docTagSee.getLinkAlias();
                link = docTagSee.getLinkName();
            }

            public String getAlias()
            {
                return alias;
            }

            public String getLink()
            {
                return link;
            }

            private final String alias;
            private final String link;
        }

        public static class DocTagParamData
        {
            public DocTagParamData(DocTagParam docTagParam)
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
        private final boolean isOneLiner;
    }

    private final List<DocCommentTemplateData> comments = new ArrayList<DocCommentTemplateData>();
}
