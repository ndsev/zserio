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
                "(docComment /** (whitespace (whitespaceInLine  )) */)");
        checkParseTree("docComment", "/** **/",
                "(docComment /** (whitespace (whitespaceInLine  )) **/)");
        checkParseTree("docComment", "/***/",
                "(docComment /** */)");
        checkParseTree("docComment", "/****/",
                "(docComment /*** */)");
        checkParseTree("docComment", "/**\n */",
                "(docComment /** (whitespace \\n ) */)");
    }

    @Test
    public void emptyCommentNewlineWithDecoration()
    {
        checkParseTree("docComment", "/**\n * */",
                "(docComment /** (whitespace \\n * ) */)");
        checkParseTree("docComment", "/**\n ** */",
                "(docComment /** (whitespace \\n ** ) */)");
        checkParseTree("docComment", "/**\n **\n **\n **/",
                "(docComment /** (whitespace \\n ** \\n ** \\n *) */)");
    }

    @Test
    public void singleLineComment()
    {
        checkParseTree("docComment", "/** Single line. */",
                "(docComment /** (whitespace (whitespaceInLine  )) " +
                "(docContent (docParagraph (docTextLine (docText (text Single (whitespaceInLine  ) line ." +
                        " (whitespaceInLine  )))))) " +
                "*/)");
    }

    @Test
    public void multiLineComment()
    {
        checkParseTree("docComment", "/**\n * Comment text\n * multiline.\n */",
                "(docComment /** (whitespace \\n * ) " +
                "(docContent " +
                    "(docParagraph " +
                        "(docTextLine (docText (text Comment (whitespaceInLine  ) text))) " +
                        "\\n *  " +
                        "(docTextLine (docText (text multiline .))))) "+
                "(whitespace \\n ) */)");
    }

    @Test
    public void multiParagraphComment()
    {
        checkParseTree("docComment", "/**\n Paragraph 1.\n *\n * Paragraph 2.\n */",
                "(docComment /** (whitespace \\n ) " +
                "(docContent " +
                    "(docParagraph (docTextLine (docText (text Paragraph (whitespaceInLine  ) 1 .)))) " +
                    "\\n * \\n *  " +
                    "(docParagraph (docTextLine (docText (text Paragraph (whitespaceInLine  ) 2 .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void multiLineMultiParagraphComment()
    {
        checkParseTree("docComment", "/**\n Paragraph 1.\n * Next line.\n *\n * Paragraph 2.\n */",
                "(docComment /** (whitespace \\n ) " +
                "(docContent " +
                    "(docParagraph " +
                        "(docTextLine (docText (text Paragraph (whitespaceInLine  ) 1 .))) " +
                        "\\n *  " +
                        "(docTextLine (docText (text Next (whitespaceInLine  ) line .)))) " +
                    "\\n * \\n *  " +
                    "(docParagraph (docTextLine (docText (text Paragraph (whitespaceInLine  ) 2 .))))) " +
                "(whitespace \\n ) */)");
    }

    @Test
    public void seeTagInText()
    {
        checkParseTree("docComment", "/** See tag @see \"alias\" identifier test. */",
                "(docComment /** (whitespace (whitespaceInLine  )) " +
                "(docContent " +
                    "(docParagraph (docTextLine (docText (text See (whitespaceInLine  ) tag " +
                    "(whitespaceInLine  ))) (docText " +
                        "(seeTag @see (whitespaceInParagraph (whitespaceInLine  )) " +
                        "(seeTagAlias \" (text alias) \") (whitespaceInParagraph (whitespaceInLine  )) " +
                        "(seeTagId identifier))) " +
                    "(docText (text (whitespaceInLine  ) test . (whitespaceInLine  )))))) " +
                "*/)");
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

    private DocComment4Parser createParser(String input)
    {
        final ThrowingErrorListener throwingErrorListener = new ThrowingErrorListener();
        final DocComment4Lexer lexer = new DocComment4Lexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(throwingErrorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final DocComment4Parser parser = new DocComment4Parser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(throwingErrorListener);
        return parser;
    }

    private void checkParseTree(String ruleName, String input, String stringTree)
    {
        final DocComment4Parser parser = createParser(input);
        try
        {
            Method rule = DocComment4Parser.class.getMethod(ruleName);
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
