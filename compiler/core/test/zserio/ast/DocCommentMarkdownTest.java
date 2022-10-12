package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import zserio.antlr.ZserioParser;
import zserio.tools.WarningsConfig;

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

        resolve(docCommentMarkdown);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See"));
        expectedLineElements.add(new ExpectedLineElement("Direction", "comments.classic_doc.enum_comments"));
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

        resolve(docCommentMarkdown);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See"));
        expectedLineElements.add(
                new ExpectedLineElement("Direction", "comments.classic_doc.enum_comments.Direction"));
        checkClassicOneLiner(docCommentMarkdown.toClassic(), expectedLineElements, "!", isSticky);
    }

    @Test
    public void markdownMultipleLinks()
    {
        final String text =
                "See [Direction](../classic_doc/enum_comments.zs#Direction) and [Color](enum_colors.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        resolve(docCommentMarkdown);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See"));
        expectedLineElements.add(
                new ExpectedLineElement("Direction", "comments.classic_doc.enum_comments.Direction"));
        expectedLineElements.add(new ExpectedLineElement("and"));
        expectedLineElements.add(new ExpectedLineElement("Color", "comments.markdown_doc.enum_colors"));
        checkClassicOneLiner(docCommentMarkdown.toClassic(), expectedLineElements, "!", isSticky);
    }

    @Test
    public void markdownMultipleLinksFirstExternal()
    {
        final String text =
                "See [Logo](image.png) and [Color](enum_colors.zs) !";
        final boolean isSticky = true;
        final boolean isOneLiner = true;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        resolve(docCommentMarkdown);

        final List<ExpectedLineElement> expectedLineElements = new ArrayList<ExpectedLineElement>();
        expectedLineElements.add(new ExpectedLineElement("See [Logo](image.png)"));
        expectedLineElements.add(new ExpectedLineElement("and"));
        expectedLineElements.add(new ExpectedLineElement("Color", "comments.markdown_doc.enum_colors"));
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
                "**case1Allowed** True if case1Field is allowed.\n" +
                "See [Direction](../classic_doc/enum_comments.zs) for more info.\n" +
                "\n" +
                "Some comment outside zserio tree: [Comment](../../../classic_doc/enum_comments.zs).\n";
        final boolean isSticky = true;
        final boolean isOneLiner = false;
        final DocCommentMarkdown docCommentMarkdown = createDocCommentMarkdown(text, isSticky, isOneLiner);

        resolve(docCommentMarkdown);

        final ArrayList<ExpectedLineElement> expectedParagraph1 = new ArrayList<ExpectedLineElement>();
        expectedParagraph1.add(new ExpectedLineElement("**TestUnion**"));

        final ArrayList<ExpectedLineElement> expectedParagraph2 = new ArrayList<ExpectedLineElement>();
        expectedParagraph2.add(new ExpectedLineElement("This is an union which uses constraint in one case."));
        expectedParagraph2.add(new ExpectedLineElement("This is an second line of the same paragraph."));

        final ArrayList<ExpectedLineElement> expectedParagraph3 = new ArrayList<ExpectedLineElement>();
        expectedParagraph3.add(new ExpectedLineElement("See [Direction](some_extern_file.html)"));
        expectedParagraph3.add(new ExpectedLineElement("page."));

        final ArrayList<ExpectedLineElement> expectedParagraph4 = new ArrayList<ExpectedLineElement>();
        expectedParagraph4.add(new ExpectedLineElement("**case1Allowed** True if case1Field is allowed."));
        expectedParagraph4.add(new ExpectedLineElement("See"));
        expectedParagraph4.add(new ExpectedLineElement("Direction", "comments.classic_doc.enum_comments"));
        expectedParagraph4.add(new ExpectedLineElement("for more info."));

        final ArrayList<ExpectedLineElement> expectedParagraph5 = new ArrayList<ExpectedLineElement>();
        expectedParagraph5.add(new ExpectedLineElement("Some comment outside zserio tree: " +
                    "[Comment](../../../classic_doc/enum_comments.zs)"));
        expectedParagraph5.add(new ExpectedLineElement("."));

        final List<ArrayList<ExpectedLineElement>> expectedParagraphs =
                new ArrayList<ArrayList<ExpectedLineElement>>();
        expectedParagraphs.add(expectedParagraph1);
        expectedParagraphs.add(expectedParagraph2);
        expectedParagraphs.add(expectedParagraph3);
        expectedParagraphs.add(expectedParagraph4);
        expectedParagraphs.add(expectedParagraph5);

        checkClassicParagraphs(docCommentMarkdown.toClassic(), expectedParagraphs, isSticky);
    }

    private DocCommentMarkdown createDocCommentMarkdown(String text, boolean isSticky, boolean isOneLiner)
    {
        return new DocCommentMarkdown(TEST_LOCATION, text, isSticky, isOneLiner);
    }

    private static class ExpectedLineElement
    {
        public ExpectedLineElement(String text)
        {
            this.text = text;
            this.seeTagAlias = null;
            this.seeTagLink = null;
        }

        public ExpectedLineElement(String seeTagAlias, String seeTagLink)
        {
            this.text = null;
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
            final DocLineElement docLineElement = docLineElementsIt.next();
            if (expectedLineElement.getText() != null)
            {
                assertNotEquals(null, docLineElement.getDocText());
                assertEquals(expectedLineElement.getText(), docLineElement.getDocText().getText());
                assertEquals(null, docLineElement.getSeeTag());
            }
            else
            {
                assertNotEquals(null, docLineElement.getSeeTag());
                assertEquals(expectedLineElement.getSeeTagAlias(), docLineElement.getSeeTag().getLinkAlias());
                assertEquals(expectedLineElement.getSeeTagLink(), docLineElement.getSeeTag().getLinkName());
                assertEquals(null, docLineElement.getDocText());
            }
        }

        assertTrue(docLineElementsIt.hasNext());
        final DocLineElement docLineElement3 = docLineElementsIt.next();
        assertEquals(expectedLineSuffix, docLineElement3.getDocText().getText());
        assertEquals(null, docLineElement3.getSeeTag());

        assertFalse(docLineElementsIt.hasNext());
    }

    private void checkClassicParagraphs(DocCommentClassic docCommentClassic,
            List<ArrayList<ExpectedLineElement>> expectedParagraphs, boolean expectedIsSticky)
    {
        assertNotEquals(null, docCommentClassic);

        assertEquals(expectedIsSticky, docCommentClassic.isSticky());
        assertEquals(false, docCommentClassic.isOneLiner());

        assertEquals(expectedParagraphs.size(), docCommentClassic.getParagraphs().size());
        for (int i = 0; i < expectedParagraphs.size(); ++i)
        {
            final DocParagraph docParagraph = docCommentClassic.getParagraphs().get(i);
            final List<ExpectedLineElement> expectedParagraph = expectedParagraphs.get(i);

            assertEquals(1, docParagraph.getDocElements().size());
            final DocElement docElement = docParagraph.getDocElements().get(0);

            assertEquals(null, docElement.getSeeTag());
            assertEquals(null, docElement.getTodoTag());
            assertEquals(null, docElement.getParamTag());
            assertEquals(null, docElement.getDeprecatedTag());

            final DocMultiline docMultiline = docElement.getDocMultiline();
            assertNotEquals(null, docMultiline);

            int lineElementsCount = 0;
            for (int j = 0; j < docMultiline.getLines().size(); ++j)
            {
                final DocLine docLine = docMultiline.getLines().get(j);
                assertTrue(expectedParagraph.size() >= docLine.getLineElements().size() + lineElementsCount);
                for (int k = 0; k < docLine.getLineElements().size(); ++k)
                {
                    final DocLineElement docLineElement = docLine.getLineElements().get(k);
                    final ExpectedLineElement expectedLine = expectedParagraph.get(lineElementsCount++);
                    if (expectedLine.getText() != null)
                    {
                        assertNotEquals(null, docLineElement.getDocText());
                        assertEquals(expectedLine.getText(), docLineElement.getDocText().getText());
                        assertEquals(null, docLineElement.getSeeTag());
                    }
                    else
                    {
                        assertNotEquals(null, docLineElement.getSeeTag());
                        assertEquals(expectedLine.getSeeTagAlias(), docLineElement.getSeeTag().getLinkAlias());
                        assertEquals(expectedLine.getSeeTagLink(), docLineElement.getSeeTag().getLinkName());
                        assertEquals(null, docLineElement.getDocText());
                    }
                }
            }
            assertEquals(expectedParagraph.size(), lineElementsCount);
        }
    }

    private void resolve(DocComment docComment)
    {
        // emulate referenced packages to avoid warnings during symbol resolving
        final LinkedHashMap<PackageName, Package> packageNameMap = new LinkedHashMap<PackageName, Package>();
        final Package enumCommentsPkg = new Package(
                new AstLocation("comments/classic_doc/enum_comments.zs", 1, 0),
                new PackageName.Builder().addId("comments").addId("classic_doc").addId("enum_comments").get(),
                new PackageName.Builder().get(), null, new ArrayList<Import>(),
                new ArrayList<DocComment>(), new ArrayList<DocComment>());
        packageNameMap.put(enumCommentsPkg.getPackageName(), enumCommentsPkg);
        final AstLocation subtypeLocation = new AstLocation("comments/classic_doc/enum_comments.zs", 2, 0);
        enumCommentsPkg.addSymbol(new Subtype(subtypeLocation, enumCommentsPkg,
                new TypeReference(subtypeLocation, enumCommentsPkg,
                        new StdIntegerType(subtypeLocation, "int8", ZserioParser.INT8)),
                "Direction", new ArrayList<DocComment>()));

        final Package enumColorsPkg = new Package(
                new AstLocation("comments/markdown_doc/enum_colors.zs", 1, 0),
                new PackageName.Builder().addId("comments").addId("markdown_doc").addId("enum_colors").get(),
                new PackageName.Builder().get(), null, new ArrayList<Import>(),
                new ArrayList<DocComment>(), new ArrayList<DocComment>());
        packageNameMap.put(enumColorsPkg.getPackageName(), enumColorsPkg);

        final List<Import> imports = new ArrayList<Import>();
        imports.add(new Import(TEST_LOCATION,
                new PackageName.Builder().addId("comments").addId("classic_doc").addId("enum_comments").get(),
                null, new ArrayList<DocComment>()));
        imports.add(new Import(TEST_LOCATION,
                new PackageName.Builder().addId("comments").addId("markdown_doc").addId("enum_colors").get(),
                null, new ArrayList<DocComment>()));
        final Package testPkg = new Package(TEST_LOCATION,
                new PackageName.Builder().addId("comments").addId("markdown_doc").addId("test").get(),
                new PackageName.Builder().get(), null, imports,
                Arrays.asList(docComment), new ArrayList<DocComment>());
        packageNameMap.put(testPkg.getPackageName(), testPkg);

        final WarningsConfig warningsConfig = new WarningsConfig();
        final Root root = new Root(packageNameMap);
        final ZserioAstImporter importer = new ZserioAstImporter(warningsConfig);
        root.accept(importer);

        final ZserioAstSymbolResolver resolver = new ZserioAstSymbolResolver(warningsConfig);
        root.accept(resolver);
    }

    final AstLocation TEST_LOCATION = new AstLocation("comments/markdown_doc/test.zs", 1, 0);
}
