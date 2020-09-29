package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final String markdown = docCommentToken.getText()
                .replaceAll("^/\\*!\\s*", "") // strip from beginning
                .replaceAll("\\s*!?\\*/$", ""); // strip from the end
        return new DocCommentMarkdown(new AstLocation(docCommentToken), markdown);
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
