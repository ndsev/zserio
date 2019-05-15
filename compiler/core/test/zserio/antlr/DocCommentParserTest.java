package zserio.antlr;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class DocCommentParserTest
{
    @Test
    public void emptyComment()
    {
        checkParseTree("docComment", "/** */",
                "(docComment /** (whitespace  ) (docContent docLine) */)");
        checkParseTree("docComment", "/** **/",
                "(docComment /** (whitespace  ) (docContent docLine) **/)");
        checkParseTree("docComment", "/***/",
                "(docComment /** (docContent docLine) */)");
        checkParseTree("docComment", "/****/",
                "(docComment /*** (docContent docLine) */)");
        checkParseTree("docComment", "/**\n */",
                "(docComment /** (whitespace \\n ) (docContent docLine) */)");
    }

    @Test
    public void emptyCommentNewlineWithDecoration()
    {
        checkParseTree("docComment", "/**\n * */",
                "(docComment /** (whitespace \\n * ) (docContent docLine) */)");
        checkParseTree("docComment", "/**\n ** */",
                "(docComment /** (whitespace \\n ** ) (docContent docLine) */)");
        checkParseTree("docComment", "/**\n **\n **\n **/",
                "(docComment /** (whitespace \\n ** \\n ** \\n *) (docContent docLine) */)");
    }

    @Test
    public void singleLineComment()
    {
        checkParseTree("docComment", "/** Single line. */",
                "(docComment /** (whitespace  ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Single (whitespaceInLine  ) line .))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void multiLineComment()
    {
        checkParseTree("docComment", "/**\n * Comment text\n * multiline.\n */",
                "(docComment /** (whitespace \\n * ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Comment (whitespaceInLine  ) text)))) " +
                        "\\n *  " +
                        "(docLine (docTextLine (docText (textElement multiline .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void multiParagraphComment()
    {
        checkParseTree("docComment", "/**\n Paragraph 1.\n *\n * Paragraph 2.\n */",
                "(docComment /** (whitespace \\n ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Paragraph (whitespaceInLine  ) 1 .)))) " +
                        "\\n * docLine \\n *  " +
                        "(docLine (docTextLine (docText (textElement Paragraph (whitespaceInLine  ) 2 .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void multiLineMultiParagraphComment()
    {
        checkParseTree("docComment", "/**\n Paragraph 1.\n * Next line.\n *\n * Paragraph 2.\n */",
                "(docComment /** (whitespace \\n ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Paragraph (whitespaceInLine  ) 1 .)))) " +
                        "\\n *  " +
                        "(docLine (docTextLine (docText (textElement Next (whitespaceInLine  ) line .)))) " +
                        "\\n * docLine \\n *  " +
                        "(docLine (docTextLine (docText (textElement Paragraph (whitespaceInLine  ) 2 .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void seeTag()
    {
        checkParseTree("docComment", "/** Hello.\n * @see \"alias\" id */",
                "(docComment /** (whitespace  ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Hello .)))) \\n *  " +
                        "(docLine (docTag (seeTag @see (whitespaceInParagraph  ) " +
                                "(seeTagAlias \" (seeTagAliasText alias) \") (whitespaceInParagraph  ) " +
                                "(seeTagId id))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void seeTagNoAlias()
    {
        checkParseTree("docComment", "/** Hello.\n * @see id */",
                "(docComment /** (whitespace  ) (docContent " +
                        "(docLine (docTextLine (docText (textElement Hello .)))) \\n *  " +
                        "(docLine (docTag (seeTag @see (whitespaceInParagraph  ) (seeTagId id))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void seeTagInText()
    {
        checkParseTree("docComment", "/** See tag @see \"alias\" identifier test. */",
                "(docComment /** (whitespace  ) (docContent " +
                        "(docLine (docTextLine (docText (textElement See (whitespaceInLine  ) tag)) " +
                        "(whitespaceInLine  ) " +
                        "(docText (seeTag @see (whitespaceInParagraph  ) " +
                                "(seeTagAlias \" (seeTagAliasText alias) \") (whitespaceInParagraph  ) " +
                                "(seeTagId identifier))) (whitespaceInLine  ) " +
                        "(docText (textElement test .))))) " +
                "(whitespace  ) */)");

        // see tag at the beginning
        checkParseTree("docComment", "/**\n * @see \"Structure A\" structA for more info.\n */",
                "(docComment /** (whitespace \\n * ) (docContent (docLine (docTextLine " +
                        "(docText (seeTag @see (whitespaceInParagraph  ) " +
                                "(seeTagAlias \" (seeTagAliasText Structure (whitespaceInLine  ) A) \") " +
                                "(whitespaceInParagraph  ) " +
                                "(seeTagId structA))) (whitespaceInLine  ) " +
                        "(docText (textElement for (whitespaceInLine  ) more (whitespaceInLine  ) info .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void paramTag()
    {
        // single line
        checkParseTree("docComment", "/** @param arg1 Description. */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTag " +
                        "(paramTag @param (whitespaceInParagraph  ) (paramName arg1) " +
                                "(whitespaceInParagraph  ) " +
                                "(docTextLine (docText (textElement Description .))))))) " +
                "(whitespace  ) */)");

        // new line before description
        checkParseTree("docComment", "/** @param arg1 \n *         Description. */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTag " +
                        "(paramTag @param (whitespaceInParagraph  ) (paramName arg1) " +
                                "(whitespaceInParagraph   \\n *                 ) " +
                                "(docTextLine (docText (textElement Description .))))))) " +
                "(whitespace  ) */)");

        // new line before both id and description
        checkParseTree("docComment", "/** @param \n *     arg1 \n *         Description.\n */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTag " +
                        "(paramTag @param (whitespaceInParagraph   \\n *         ) (paramName arg1) " +
                                "(whitespaceInParagraph   \\n *                 ) " +
                                "(docTextLine (docText (textElement Description .))))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void paramTagAsText()
    {
        checkParseTree("docComment", "/** This is not @param p description param tag! */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTextLine " +
                        "(docText (textElement This (whitespaceInLine  ) is (whitespaceInLine  ) not)) " +
                        "(whitespaceInLine  ) (docText (textElement @param)) (whitespaceInLine  ) " +
                        "(docText (textElement p (whitespaceInLine  ) description (whitespaceInLine  ) param " +
                        "(whitespaceInLine  ) tag!))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void todoTag()
    {
        checkParseTree("docComment", "/** @todo This is todo. Do it!. */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTag " +
                        "(todoTag @todo (whitespaceInParagraph  ) (docTextLine (docText " +
                                "(textElement This (whitespaceInLine  ) is (whitespaceInLine  ) " +
                                "todo . (whitespaceInLine  ) Do (whitespaceInLine  ) it! .))))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void todoTagAsText()
    {
        checkParseTree("docComment", "/** This is not @todo tag! */",
                "(docComment /** (whitespace  ) (docContent (docLine (docTextLine " +
                        "(docText (textElement This (whitespaceInLine  ) is (whitespaceInLine  ) not)) " +
                        "(whitespaceInLine  ) (docText (textElement @todo)) (whitespaceInLine  ) " +
                        "(docText (textElement tag!))))) " +
                "(whitespace  ) */)");
    }

    @Test
    public void deprecatedTag()
    {
        checkParseTree("docComment", "/**\n * @deprecated\n */",
                "(docComment /** (whitespace \\n * ) (docContent (docLine " +
                        "(docTag (deprecatedTag @deprecated)))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void deprecatedTagAsText()
    {
        checkParseTree("docComment", "/**\n * This is not @deprecated tag.\n */",
                "(docComment /** (whitespace \\n * ) (docContent (docLine (docTextLine " +
                        "(docText (textElement This (whitespaceInLine  ) is (whitespaceInLine  ) not)) " +
                        "(whitespaceInLine  ) (docText (textElement @deprecated)) (whitespaceInLine  ) " +
                        "(docText (textElement tag .))))) " +
                "(whitespace \\n ) */)");

        // with pending text
        checkParseTree("docComment", "/**\n * @deprecated with pending text.\n */",
                "(docComment /** (whitespace \\n * ) (docContent (docLine (docTextLine " +
                        "(docText (textElement @deprecated)) (whitespaceInLine  ) " +
                        "(docText (textElement with (whitespaceInLine  ) pending " +
                        "(whitespaceInLine  ) text .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void specialCharacters()
    {
        checkParseTree("docComment", "/** .\t\r\n\"*\\ \\\\ ?!:;+- */",
                "(docComment /** (whitespace  ) (docContent " +
                        "(docLine (docTextLine (docText (textElement .)))) (whitespaceInLine \\t) \\r\\n " +
                        "(docLine (docTextLine (docText (textElement \" * \\ (whitespaceInLine  ) \\\\" +
                                " (whitespaceInLine  ) ?!:;+-))))) " +
                "(whitespace  ) */)");
    }

    private static class ThrowingErrorListener extends BaseErrorListener
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine, String msg,
                RecognitionException e) throws ParseCancellationException
        {
            throw new ParseCancellationException(msg);
        }
    }

    private DocCommentParser createParser(String input)
    {
        final ThrowingErrorListener throwingErrorListener = new ThrowingErrorListener();
        final DocCommentLexer lexer = new DocCommentLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(throwingErrorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final DocCommentParser parser = new DocCommentParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(throwingErrorListener);
        return parser;
    }

    private void checkParseTree(String ruleName, String input, String stringTree)
    {
        final DocCommentParser parser = createParser(input);
        try
        {
            Method rule = DocCommentParser.class.getMethod(ruleName);
            ParseTree tree = (ParseTree)(rule.invoke(parser));
            assertEquals(stringTree, tree.toStringTree(parser));
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
    }
}
