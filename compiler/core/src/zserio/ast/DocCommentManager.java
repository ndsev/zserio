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
     * @param terminalNode Terminal node.
     *
     * @return List of parsed documentation comment.
     */
    public List<DocComment> findDocComments(TerminalNode terminalNode)
    {
        return (terminalNode == null) ? new ArrayList<DocComment>() :
            findDocCommentsBefore(terminalNode.getSymbol());
    }

    /**
     * Finds appropriate documentation comments which belongs to the given context.
     *
     * @param ctx Parser context.
     *
     * @return List of parsed documentation comment.
     */
    public List<DocComment> findDocComments(ParserRuleContext ctx)
    {
        return (ctx == null) ? new ArrayList<DocComment>() : findDocCommentsBefore(ctx.getStart());
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
        List<DocComment> docComments = new ArrayList<DocComment>();

        // before field alignment
        docComments.addAll(findDocComments(ctx.fieldAlignment()));
        // before field offset
        docComments.addAll(findDocComments(ctx.fieldOffset()));
        // before optional keyword
        docComments.addAll(findDocComments(ctx.OPTIONAL()));
        // before field type
        docComments.addAll(findDocComments(ctx.fieldTypeId()));

        return docComments;
    }

    private List<DocComment> findDocCommentsBefore(Token token)
    {
        final List<DocComment> docComments = new ArrayList<DocComment>();
        if (currentTokenStream == null)
            return docComments;

        final int tokenIndex = token.getTokenIndex();
        final List<Token> docCommentTokens = currentTokenStream.getHiddenTokensToLeft(tokenIndex,
                ZserioLexer.DOC);
        if (docCommentTokens == null)
            return docComments;

        final int numDocCommentTokens = docCommentTokens.size();
        for (int i = 0; i < numDocCommentTokens; ++i)
        {
            final Token docCommentToken = docCommentTokens.get(i);
            currentUsedComments.add(docCommentToken);

            final Token followingToken = (i + 1 == numDocCommentTokens) ? token : docCommentTokens.get(i + 1);
            final String[] docCommentLines = docCommentToken.getText().split("\\n");
            final int lineAfterDocComment = docCommentToken.getLine() + docCommentLines.length;
            final boolean isSticky = (followingToken.getLine() > lineAfterDocComment) ? false : true;
            final DocComment docComment = parseDocComment(docCommentToken, docCommentLines, isSticky);
            if (docComment != null) // if parsed properly
                docComments.add(docComment);
        }

        return docComments;
    }

    private static DocComment parseDocComment(Token docCommentToken, String[] docCommentLines, boolean isSticky)
    {
        final boolean isOneLiner = (docCommentLines.length == 1) ? true : false;
        if (docCommentToken.getType() == ZserioLexer.MARKDOWN_COMMENT)
            return parseDocCommentMarkdown(docCommentToken, docCommentLines, isSticky, isOneLiner);
        else
            return parseDocCommentClassic(docCommentToken, isSticky, isOneLiner);
    }

    private static DocCommentMarkdown parseDocCommentMarkdown(Token docCommentToken, String[] docCommentLines,
            boolean isSticky, boolean isOneLiner)
    {
        String markdown = docCommentToken.getText();
        if (docCommentLines.length > 1 && docCommentLines[0].trim().equals("/*!"))
        {
            // there are at least two lines and the first line contains only comment syntax "/*!"
            final int commentIndentInSpaces = new AstLocation(docCommentToken).getColumn() - 1;
            final String indent = getFirstLineIndent(docCommentLines, commentIndentInSpaces);
            if (!indent.isEmpty() && areLinesIndented(docCommentLines, indent))
            {
                // strip the indent from each line, (?m) is multiline regex marker
                markdown = markdown.replaceAll("(?m)^" + indent, "");
            }
        }

        markdown = markdown
                .replaceFirst("^\\/\\*!", "") // strip comment marker from beginning
                .replaceFirst("!?\\*\\/$", ""); // strip comment marker from the end

        return new DocCommentMarkdown(new AstLocation(docCommentToken), markdown, isSticky, isOneLiner);
    }

    private static String getFirstLineIndent(String[] lines, int numWhitespaces)
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

    private static boolean areLinesIndented(String[] lines, String indent)
    {
        for (int i = 1; i < lines.length; i++)
        {
            if (!lines[i].isEmpty() && !lines[i].startsWith(indent))
                    return false;
        }

        return true;
    }

    private static DocCommentClassic parseDocCommentClassic(Token docCommentToken, boolean isSticky,
            boolean isOneLiner)
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

            final DocCommentAstBuilder docCommentAstBuilder =
                    new DocCommentAstBuilder(docCommentToken, isSticky, isOneLiner);
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
