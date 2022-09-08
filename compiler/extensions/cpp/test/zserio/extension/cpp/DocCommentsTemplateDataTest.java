package zserio.extension.cpp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import zserio.ast.AstLocation;
import zserio.ast.DocComment;
import zserio.ast.DocCommentMarkdown;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData.DocMultilineData;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData.DocMultilineData.DocLineData;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData.DocMultilineData.DocLineElementData;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData.DocParagraphData;
import zserio.extension.cpp.DocCommentsTemplateData.DocCommentTemplateData.DocParagraphData.DocElementData;

public class DocCommentsTemplateDataTest
{
    @Test
    public void markdownLink()
    {
        final String text =
                "See [Direction](../classic_doc/enum_comments.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<DocComment> docComments = new ArrayList<DocComment>();
        docComments.add(docCommentMarkdown);
        final DocCommentsTemplateData templateData = new DocCommentsTemplateData(docComments);
        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction", "classic_doc::enum_comments"));
        checkDocTemplateDataLine(templateData, expectedLineElements, "!");
    }

    @Test
    public void markdownLinkWithType()
    {
        final String text =
                "See [Direction](../classic_doc/enum_comments.zs#Direction) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<DocComment> docComments = new ArrayList<DocComment>();
        docComments.add(docCommentMarkdown);
        final DocCommentsTemplateData templateData = new DocCommentsTemplateData(docComments);
        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction",
                "classic_doc::enum_comments::Direction"));
        checkDocTemplateDataLine(templateData, expectedLineElements, "!");
    }

    @Test
    public void markdownMultipleLinks()
    {
        final String text =
                "See [Direction](enum_comments.zs#Direction) and [Color](enum_colors.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<DocComment> docComments = new ArrayList<DocComment>();
        docComments.add(docCommentMarkdown);
        final DocCommentsTemplateData templateData = new DocCommentsTemplateData(docComments);
        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction", "enum_comments::Direction"));
        expectedLineElements.add(new ExpectedLineElement("and", "Color", "enum_colors"));
        checkDocTemplateDataLine(templateData, expectedLineElements, "!");
    }

    private DocCommentMarkdown createDocCommentMarkdown(String text, boolean isSticky, boolean isOneLiner)
    {
        final AstLocation location = new AstLocation("test.zs", 0 , 0);

        return new DocCommentMarkdown(location, text, isSticky, isOneLiner);
    }

    private static class ExpectedLineElement
    {
        public ExpectedLineElement(String text, String seeTagAlias, String seeTagLink)
        {
            this.text = text;
            this.seeTagAlias = seeTagAlias;
            this.seeTagLink = seeTagLink;
        }

        public String getText()
        {
            return text;
        }

        public String getSeeTagAlias()
        {
            return seeTagAlias;
        }

        public String getSeeTagLink()
        {
            return seeTagLink;
        }

        private String text;
        private String seeTagAlias;
        private String seeTagLink;
    }

    private void checkDocTemplateDataLine(DocCommentsTemplateData templateData,
            List<ExpectedLineElement> expectedLineElements, String lineSuffix)
    {
        assertNotEquals(null, templateData);

        final Iterator<DocCommentTemplateData> commentTemplateDataIt = templateData.getComments().iterator();
        assertTrue(commentTemplateDataIt.hasNext());
        final DocCommentTemplateData commentTemplateData = commentTemplateDataIt.next();
        assertFalse(commentTemplateDataIt.hasNext());
        assertEquals(true, commentTemplateData.getIsOneLiner());

        final Iterator<DocParagraphData> paragraphTemplateDataIt =
                commentTemplateData.getParagraphs().iterator();
        assertTrue(paragraphTemplateDataIt.hasNext());
        final DocParagraphData paragraphTemplateData = paragraphTemplateDataIt.next();
        assertFalse(paragraphTemplateDataIt.hasNext());

        final Iterator<DocElementData> elementsTemplateDataIt =
                paragraphTemplateData.getElements().iterator();
        assertTrue(elementsTemplateDataIt.hasNext());
        final DocElementData elementTemplateData = elementsTemplateDataIt.next();
        assertFalse(elementsTemplateDataIt.hasNext());

        assertEquals(null, elementTemplateData.getSeeTag());
        assertEquals(null, elementTemplateData.getTodoTag());
        assertEquals(null, elementTemplateData.getParamTag());
        assertEquals(false, elementTemplateData.getIsDeprecated());

        final DocMultilineData docMultilineData = elementTemplateData.getMultiline();
        assertNotEquals(null, docMultilineData);

        final Iterator<DocLineData> linesTemplateDataIt = docMultilineData.getLines().iterator();
        assertTrue(linesTemplateDataIt.hasNext());
        final DocLineData lineTemplateData = linesTemplateDataIt.next();
        assertFalse(linesTemplateDataIt.hasNext());

        final Iterator<DocLineElementData> lineElementsTemplateDataIt =
                lineTemplateData.getLineElements().iterator();

        for (ExpectedLineElement expectedLineElement : expectedLineElements)
        {
            assertTrue(lineElementsTemplateDataIt.hasNext());
            final DocLineElementData lineElementTemplateData1 = lineElementsTemplateDataIt.next();
            assertEquals(expectedLineElement.getText(), lineElementTemplateData1.getDocString());
            assertEquals(null, lineElementTemplateData1.getSeeTag());

            assertTrue(lineElementsTemplateDataIt.hasNext());
            final DocLineElementData lineElementTemplateData2 = lineElementsTemplateDataIt.next();
            assertEquals(null, lineElementTemplateData2.getDocString());
            assertEquals(expectedLineElement.getSeeTagAlias(), lineElementTemplateData2.getSeeTag().getAlias());
            assertEquals(expectedLineElement.getSeeTagLink(), lineElementTemplateData2.getSeeTag().getLink());
        }

        assertTrue(lineElementsTemplateDataIt.hasNext());
        final DocLineElementData lineElementTemplateData3 = lineElementsTemplateDataIt.next();
        assertEquals(lineSuffix, lineElementTemplateData3.getDocString());
        assertEquals(null, lineElementTemplateData3.getSeeTag());

        assertFalse(lineElementsTemplateDataIt.hasNext());
    }
}
