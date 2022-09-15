package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DocCommentMarkdownTest
{
    @Test
    public void markdownLink()
    {
        final String text =
                "See [Direction](../classic_doc/enum_comments.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction", "classic_doc.enum_comments"));
        checkClassicOneLiner(docCommentMarkdown.toClassic(), expectedLineElements, "!", isSticky);
    }

    @Test
    public void markdownLinkWithType()
    {
        final String text =
                "See [Direction](../classic_doc/enum_comments.zs#Direction) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction",
                "classic_doc.enum_comments.Direction"));
        checkClassicOneLiner(docCommentMarkdown.toClassic(), expectedLineElements, "!", isSticky);
    }

    @Test
    public void markdownMultipleLinks()
    {
        final String text =
                "See [Direction](enum_comments.zs#Direction) and [Color](enum_colors.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See", "Direction", "enum_comments.Direction"));
        expectedLineElements.add(new ExpectedLineElement("and", "Color", "enum_colors"));
        checkClassicOneLiner(docCommentMarkdown.toClassic(), expectedLineElements, "!", isSticky);
    }

    @Test
    public void markdownMultipleParagraphs()
    {
        final String text =
                "**TestUnion**\n" +
                "\n" +
                "This is an union which uses constraint in one case.\n" +
                "This is an second line of the same paragraph.\n" +
                "\n" +
                "\n" +
                "See [Direction](some_extern_file.html) page.\n" +
                "\n" +
                "**case1Allowed** True if case1Field is allowed.\n";
        final boolean isSticky = true;
        final boolean isOneLiner = false;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        final ArrayList<String> expectedParagraph1 = new ArrayList<String>();
        expectedParagraph1.add("**TestUnion**");

        final ArrayList<String> expectedParagraph2 = new ArrayList<String>();
        expectedParagraph2.add("This is an union which uses constraint in one case.");
        expectedParagraph2.add("This is an second line of the same paragraph.");

        final ArrayList<String> expectedParagraph3 = new ArrayList<String>();
        expectedParagraph3.add("See [Direction](some_extern_file.html) page.");

        final ArrayList<String> expectedParagraph4 = new ArrayList<String>();
        expectedParagraph4.add("**case1Allowed** True if case1Field is allowed.");

        final List<ArrayList<String>> expectedParagraphs = new ArrayList<ArrayList<String>>();
        expectedParagraphs.add(expectedParagraph1);
        expectedParagraphs.add(expectedParagraph2);
        expectedParagraphs.add(expectedParagraph3);
        expectedParagraphs.add(expectedParagraph4);

        checkClassicParagraphs(docCommentMarkdown.toClassic(), expectedParagraphs, isSticky);
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

    private void checkClassicOneLiner(DocCommentClassic docCommentClassic,
            List<ExpectedLineElement> expectedLineElements, String expectedLineSuffix, boolean expectedIsSticky)
    {
        assertNotEquals(null, docCommentClassic);

        assertEquals(expectedIsSticky, docCommentClassic.isSticky());
        assertEquals(true, docCommentClassic.isOneLiner());

        assertEquals(1, docCommentClassic.getParagraphs().size());
        final DocParagraph docParagraph = docCommentClassic.getParagraphs().get(0);

        assertEquals(1, docParagraph.getDocElements().size());
        final DocElement docElement = docParagraph.getDocElements().get(0);

        assertEquals(null, docElement.getSeeTag());
        assertEquals(null, docElement.getTodoTag());
        assertEquals(null, docElement.getParamTag());
        assertEquals(null, docElement.getDeprecatedTag());

        final DocMultiline docMultiline = docElement.getDocMultiline();
        assertNotEquals(null, docMultiline);

        assertEquals(1, docMultiline.getLines().size());
        final DocLine docLine = docMultiline.getLines().get(0);

        final Iterator<DocLineElement> docLineElementsIt = docLine.getLineElements().iterator();
        for (ExpectedLineElement expectedLineElement : expectedLineElements)
        {
            assertTrue(docLineElementsIt.hasNext());
            final DocLineElement docLineElement1 = docLineElementsIt.next();
            assertEquals(expectedLineElement.getText(), docLineElement1.getDocText().getText());
            assertEquals(null, docLineElement1.getSeeTag());

            assertTrue(docLineElementsIt.hasNext());
            final DocLineElement docLineElement2 = docLineElementsIt.next();
            assertEquals(null, docLineElement2.getDocText());
            assertEquals(expectedLineElement.getSeeTagAlias(), docLineElement2.getSeeTag().getLinkAlias());
            assertEquals(expectedLineElement.getSeeTagLink(), docLineElement2.getSeeTag().getLinkName());
        }

        assertTrue(docLineElementsIt.hasNext());
        final DocLineElement docLineElement3 = docLineElementsIt.next();
        assertEquals(expectedLineSuffix, docLineElement3.getDocText().getText());
        assertEquals(null, docLineElement3.getSeeTag());

        assertFalse(docLineElementsIt.hasNext());
    }

    private void checkClassicParagraphs(DocCommentClassic docCommentClassic,
            List<ArrayList<String>> expectedParagraphs, boolean expectedIsSticky)
    {
        assertNotEquals(null, docCommentClassic);

        assertEquals(expectedIsSticky, docCommentClassic.isSticky());
        assertEquals(false, docCommentClassic.isOneLiner());

        assertEquals(expectedParagraphs.size(), docCommentClassic.getParagraphs().size());
        for (int i = 0; i < expectedParagraphs.size(); ++i)
        {
            final DocParagraph docParagraph = docCommentClassic.getParagraphs().get(i);
            final List<String> expectedParagraph = expectedParagraphs.get(i);

            assertEquals(1, docParagraph.getDocElements().size());
            final DocElement docElement = docParagraph.getDocElements().get(0);

            assertEquals(null, docElement.getSeeTag());
            assertEquals(null, docElement.getTodoTag());
            assertEquals(null, docElement.getParamTag());
            assertEquals(null, docElement.getDeprecatedTag());

            final DocMultiline docMultiline = docElement.getDocMultiline();
            assertNotEquals(null, docMultiline);

            assertEquals(expectedParagraph.size(), docMultiline.getLines().size());
            for (int j = 0; j < expectedParagraph.size(); ++j)
            {
                final DocLine docLine = docMultiline.getLines().get(j);
                final String expectedLine = expectedParagraph.get(j);

                assertEquals(1, docLine.getLineElements().size());
                final DocLineElement docLineElement = docLine.getLineElements().get(0);
                assertEquals(expectedLine, docLineElement.getDocText().getText());
                assertEquals(null, docLineElement.getSeeTag());
            }
        }
    }
}
