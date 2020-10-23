package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import zserio.antlr.DocCommentLexer;
import zserio.antlr.DocCommentParser;
import zserio.antlr.ZserioLexer;
import zserio.antlr.ZserioParser;
import zserio.antlr.util.TokenParseErrorListener;
import zserio.tools.ZserioToolPrinter;

/**
 * Documentation comment manager which helps to find and parse usable documentation comments.
 *
 * The manager also detects unused documentation comments.
 */
class DocCommentManager
{
    /**
     * Sets the current token stream.
     *
     * @param tokenStream Current token stream.
     */
    public void setStream(BufferedTokenStream tokenStream)
    {
        currentTokenStream = tokenStream;
    }

    /**
     * Prints warnings for unused documentation comments.
     */
    public void printWarnings()
    {
        if (currentTokenStream == null)
            return;

        for (int i = 0; i < currentTokenStream.size(); ++i)
        {
            final Token token = currentTokenStream.get(i);
            if (token.getChannel() == ZserioLexer.DOC)
            {
                if (!currentUsedComments.contains(token))
                {
                    ZserioToolPrinter.printWarning(new AstLocation(token),
                            "Documentation comment is not used!");
                }
            }
        }
    }

    /**
     * Resets the current token stream.
     */
    public void resetStream()
    {
        currentTokenStream = null;
        currentUsedComments.clear();
    }

    /**
     * Finds appropriate documentation comments which belongs to the given terminal node.
     *
     * @param ctx Terminal node.
     *
     * @return List of parsed documentation comment or null.
     */
    public List<DocComment> findDocComments(TerminalNode node)
    {
        final List<Token> docTokens = findDocTokensBefore(node);
        return parseDocComments(docTokens);
    }

    /**
     * Finds appropriate documentation comments which belongs to the given context.
     *
     * @param ctx Parser context.
     *
     * @return List of parsed documentation comment or null.
     */
    public List<DocComment> findDocComments(ParserRuleContext ctx)
    {
        final List<Token> docTokens = findDocTokensBefore(ctx);
        return parseDocComments(docTokens);
    }

    /**
     * Finds appropriate documentation comments which belongs to the given context.
     * Overloaded version for structure field.
     *
     * @param ctx Parser context for structure field definition.
     *
     * @return List of parsed documentation comments.
     */
    public List<DocComment> findDocComments(ZserioParser.StructureFieldDefinitionContext ctx)
    {
        List<Token> docTokens = new ArrayList<Token>();

        // before field alignment
        docTokens.addAll(findDocTokensBefore(ctx.fieldAlignment()));
        // before field offset
        docTokens.addAll(findDocTokensBefore(ctx.fieldOffset()));
        // before optional keyword
        docTokens.addAll(findDocTokensBefore(ctx.OPTIONAL()));
        // before field type
        docTokens.addAll(findDocTokensBefore(ctx.fieldTypeId()));

        return parseDocComments(docTokens);
    }

    private List<Token> findDocTokensBefore(ParserRuleContext ctx)
    {
        return (ctx == null) ? new ArrayList<Token>() : findDocTokensBefore(ctx.getStart());
    }

    private List<Token> findDocTokensBefore(TerminalNode terminalNode)
    {
        return (terminalNode == null) ? new ArrayList<Token>() : findDocTokensBefore(terminalNode.getSymbol());
    }

    private List<Token> findDocTokensBefore(Token token)
    {
        if (currentTokenStream == null)
            return null;

        final int tokenIndex = token.getTokenIndex();
        final List<Token> docTokens = currentTokenStream.getHiddenTokensToLeft(tokenIndex, ZserioLexer.DOC);
        if (docTokens == null)
            return new ArrayList<Token>();
        for (Token docToken : docTokens)
            currentUsedComments.add(docToken);
        return docTokens;
    }

    private List<DocComment> parseDocComments(List<Token> docTokens)
    {
        List<DocComment> docComments = new ArrayList<DocComment>();
        for (Token docToken : docTokens)
        {
            final DocComment docComment = parseDocComment(docToken);
            if (docComment != null) // if parsed properly
                docComments.add(docComment);
        }
        return docComments;
    }

    private DocComment parseDocComment(Token docCommentToken)
    {
        if (docCommentToken.getType() == ZserioLexer.MARKDOWN_COMMENT)
            return parseDocCommentMarkdown(docCommentToken);
        else
            return parseDocCommentClassic(docCommentToken);
    }

    private DocCommentMarkdown parseDocCommentMarkdown(Token docCommentToken)
    {
        String markdown = docCommentToken.getText();
        final String[] lines = markdown.split("\\n");
        if (lines.length > 1 && lines[0].trim().equals("/*!"))
        {
            // there are at least two lines and the first line contains only comment syntax "/*!"
            final int commentIndentInSpaces = new AstLocation(docCommentToken).getColumn() - 1;
            final String indent = getFirstLineIndent(lines, commentIndentInSpaces);
            if (!indent.isEmpty() && areLinesIndented(lines, indent))
            {
                // strip the indent from each line, (?m) is multiline regex marker
                markdown = markdown.replaceAll("(?m)^" + indent, "");
            }
        }

        markdown = markdown
                .replaceFirst("^\\/\\*!", "") // strip comment marker from beginning
                .replaceFirst("!?\\*\\/$", ""); // strip comment marker from the end

        return new DocCommentMarkdown(new AstLocation(docCommentToken), markdown);
    }

    private String getFirstLineIndent(String[] lines, int numWhitespaces)
    {
        String indent = "";
        for (int i = 1; i < lines.length; i++)
        {
            if (!lines[i].isEmpty())
            {
                final Matcher indentMatcher =
                        Pattern.compile("^[ \\t]{" + numWhitespaces + "}").matcher(lines[i]);
                if (indentMatcher.find())
                    indent = indentMatcher.group();
                break;
            }
        }

        return indent;
    }

    private boolean areLinesIndented(String[] lines, String indent)
    {
        for (int i = 1; i < lines.length; i++)
        {
            if (!lines[i].isEmpty() && !lines[i].startsWith(indent))
                    return false;
        }

        return true;
    }

    private DocCommentClassic parseDocCommentClassic(Token docCommentToken)
    {
        try
        {
            final CharStream inputStream = CharStreams.fromString(docCommentToken.getText());
            final TokenParseErrorListener parseErrorListener = new TokenParseErrorListener(docCommentToken);
            final DocCommentLexer lexer = new DocCommentLexer(inputStream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(parseErrorListener);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final DocCommentParser parser = new DocCommentParser(tokenStream);
            parser.removeErrorListeners();
            parser.addErrorListener(parseErrorListener);

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

            final DocCommentAstBuilder docCommentAstBuilder = new DocCommentAstBuilder(docCommentToken);
            final DocCommentClassic docComment = (DocCommentClassic)docCommentAstBuilder.visit(tree);

            return docComment;
        }
        catch (ParserException e)
        {
            // if we cannot parse comment, just ignore it and report warning
            ZserioToolPrinter.printWarning(e.getLocation(), "Documentation: " +
                    e.getMessage() + "!");
            return null;
        }
    }

    private BufferedTokenStream currentTokenStream = null;
    private final Set<Token> currentUsedComments = new HashSet<Token>();
}
