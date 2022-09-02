package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import zserio.antlr.DocCommentLexer;
import zserio.antlr.DocCommentParser;

public class DocCommentClassicTest
{
    @Test
    public void findParamDoc()
    {
        final String text =
                "/**\n" +
                " *\n" +
                " * This is a classic documentation comment.\n" +
                " *\n" +
                " * @param hasExtraValue True if the structure has extra value.\n" +
                " *        This extra line contains additional comment for parameter hasExtraValue.\n" +
                " * @param anotherParam Another parameter description\n" +
                " *\n" +
                " * @return Value\n" +
                " */";
        final boolean isSticky = true;
        final boolean isOneLiner = false;
        final DocCommentClassic docCommentClassic = createDocCommentClassic(text, isSticky, isOneLiner);

        final DocComment unknownComment = docCommentClassic.findParamDoc("unknown");
        assertEquals(null, unknownComment);

        final DocComment hasExtraValueComment = docCommentClassic.findParamDoc("hasExtraValue");
        checkParamComment(hasExtraValueComment, "True if the structure has extra value.\n" +
                "This extra line contains additional comment for parameter hasExtraValue.\n", false);

        final DocComment anotherParamComment = docCommentClassic.findParamDoc("anotherParam");
        checkParamComment(anotherParamComment, "Another parameter description\n", true);
    }

    private DocCommentClassic createDocCommentClassic(String text, boolean isSticky, boolean isOneLiner)
    {
        try
        {
            final CharStream inputStream = CharStreams.fromString(text);
            final DocCommentLexer lexer = new DocCommentLexer(inputStream);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final DocCommentParser parser = new DocCommentParser(tokenStream);

            ParseTree tree = null;
            try
            {
                parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                tree = parser.docComment();
            }
            catch (ParserException e)
            {
                tokenStream.seek(0);
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                tree = parser.docComment();
            }

            final Token docCommentToken = lexer.getToken();
            final DocCommentAstBuilder docCommentAstBuilder =
                    new DocCommentAstBuilder(docCommentToken, isSticky, isOneLiner);
            final DocCommentClassic docComment = (DocCommentClassic)docCommentAstBuilder.visit(tree);

            return docComment;
        }
        catch (ParserException e)
        {
            fail("Cannot create doc comment from '" + text + "'!");
            return null;
        }
    }

    private void checkParamComment(DocComment paramComment, String expectedComment, boolean isOneLiner)
    {
        assertNotEquals(null, paramComment);
        assertEquals(true, paramComment.isSticky());
        assertEquals(isOneLiner, paramComment.isOneLiner());

        assertTrue(paramComment instanceof DocCommentClassic);
        final DocCommentClassic paramCommentClassic = (DocCommentClassic)paramComment;

        assertEquals(1, paramCommentClassic.getParagraphs().size());
        final DocParagraph paramParagraph = paramCommentClassic.getParagraphs().get(0);

        assertEquals(1, paramParagraph.getDocElements().size());
        final DocElement paramElement = paramParagraph.getDocElements().get(0);

        assertEquals(null, paramElement.getSeeTag());
        assertEquals(null, paramElement.getTodoTag());
        assertEquals(null, paramElement.getParamTag());
        assertEquals(null, paramElement.getDeprecatedTag());

        final DocMultiline paramMultiline = paramElement.getDocMultiline();
        assertNotEquals(null, paramMultiline);
        final String[] expectedLines = expectedComment.split("\n");
        final List<DocLine> paramLines = paramMultiline.getLines();
        assertEquals(expectedLines.length, paramLines.size());
        for (int i = 0; i < expectedLines.length; ++i)
        {
            assertEquals(1, paramLines.get(i).getLineElements().size());
            assertEquals(expectedLines[i], paramLines.get(i).getLineElements().get(0).getDocText().getText());
        }
    }
}
