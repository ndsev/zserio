package zserio.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import zserio.antlr.DocCommentLexer;
import zserio.antlr.DocCommentParser;
import zserio.antlr.ZserioLexer;
import zserio.antlr.ZserioParser;
import zserio.antlr.util.ParseCancellingErrorListener;
import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * Documentation comment manager which helps to find and parse usable documentation comments.
 *
 * The manager also detects unused documentation comments.
 */
public class DocCommentManager
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
                    ZserioToolPrinter.printWarning(new AstLocation(token), "Unused documentation comment!");
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
     * Finds appropriate documentation comment which belongs to the given context.
     *
     * @param ctx Parser context.
     *
     * @return Parsed documentation comment or null.
     */
    public DocComment findDocComment(ParserRuleContext ctx)
    {
        final Token docToken = findDocTokenBefore(ctx);
        if (docToken != null)
            return parseDocComment(docToken);

        return null;
    }

    /**
     * Finds appropriate documentation comment which belongs to the given context.
     * Overloaded version for structure field.
     *
     * @param ctx Parser context.
     *
     * @return Parsed documentation comment or null.
     */
    public DocComment findDocComment(ZserioParser.StructureFieldDefinitionContext ctx)
    {
        // before field type
        Token docToken = findDocTokenBefore(ctx.fieldTypeId());
        if (docToken != null)
            return parseDocComment(docToken);

        // before field optional
        docToken = findDocTokenBefore(ctx.OPTIONAL());
        if (docToken != null)
            return parseDocComment(docToken);

        // before field offset
        docToken = findDocTokenBefore(ctx.fieldOffset());
        if (docToken != null)
            return parseDocComment(docToken);

        // before field alignment
        docToken = findDocTokenBefore(ctx.fieldAlignment());
        if (docToken != null)
            return parseDocComment(docToken);

        return null;
    }

    private Token findDocTokenBefore(ParserRuleContext ctx)
    {
        return (ctx == null) ? null : findDocTokenBefore(ctx.getStart());
    }

    private Token findDocTokenBefore(TerminalNode terminalNode)
    {
        return (terminalNode == null) ? null : findDocTokenBefore(terminalNode.getSymbol());
    }

    private Token findDocTokenBefore(Token token)
    {
        if (currentTokenStream == null)
            return null;

        final int tokenIndex = token.getTokenIndex();
        final List<Token> docList = currentTokenStream.getHiddenTokensToLeft(tokenIndex, ZserioLexer.DOC);
        if (docList != null && !docList.isEmpty())
        {
            final Token docToken = docList.get(docList.size() - 1);
            currentUsedComments.add(docToken);
            return docToken;
        }

        return null;
    }

    private DocComment parseDocComment(Token docCommentToken)
    {
        try
        {
            final CharStream inputStream = CharStreams.fromString(docCommentToken.getText());
            final ParseCancellingErrorListener parseCancellingErrorListener =
                    new ParseCancellingErrorListener(docCommentToken);
            final DocCommentLexer lexer = new DocCommentLexer(inputStream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(parseCancellingErrorListener);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final DocCommentParser parser = new DocCommentParser(tokenStream);
            parser.removeErrorListeners();
            parser.addErrorListener(parseCancellingErrorListener);
            final ParseTree tree = parser.docComment();

            final DocCommentAstBuilder docCommentAstBuilder = new DocCommentAstBuilder(docCommentToken);
            return (DocComment)docCommentAstBuilder.visit(tree);
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
