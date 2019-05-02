package zserio.ast4.doc;

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

import zserio.antlr.DocComment4Lexer;
import zserio.antlr.DocComment4Parser;
import zserio.antlr.Zserio4Lexer;
import zserio.antlr.Zserio4Parser;
import zserio.ast4.AstNodeLocation;
import zserio.tools.ZserioToolPrinter;

public class DocCommentFinder
{
    public void setStream(BufferedTokenStream tokenStream)
    {
        currentTokenStream = tokenStream;
    }

    public void printUnusedWarnings()
    {
        if (currentTokenStream == null)
            return;

        for (int i = 0; i < currentTokenStream.size(); ++i)
        {
            final Token token = currentTokenStream.get(i);
            if (token.getChannel() == Zserio4Lexer.DOC)
            {
                if (!currentUsedComments.contains(token))
                    ZserioToolPrinter.printWarning(new AstNodeLocation(token), "Unused documentation comment!");
            }
        }
    }

    public void resetStream()
    {
        currentTokenStream = null;
        currentUsedComments.clear();
    }

    public DocComment findDocComment(ParserRuleContext ctx)
    {
        final Token docToken = findDocTokenBefore(ctx);
        if (docToken != null)
            return parseDocComment(docToken);

        return null;
    }

    public DocComment findDocComment(Zserio4Parser.StructureFieldDefinitionContext ctx)
    {
        // before field type
        Token docToken = findDocTokenBefore(ctx.fieldTypeId());
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
        if (ctx == null || currentTokenStream == null)
            return null;

        final int tokenIndex = ctx.getStart().getTokenIndex();
        final List<Token> docList = currentTokenStream.getHiddenTokensToLeft(tokenIndex, Zserio4Lexer.DOC);
        if (docList != null && !docList.isEmpty())
        {
            final Token docToken = docList.get(docList.size() - 1); // TODO: which order?
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
            final DocComment4Lexer lexer = new DocComment4Lexer(inputStream);
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final DocComment4Parser parser = new DocComment4Parser(tokenStream);
            ParseTree tree = parser.docComment();

            final DocCommentAstBuilder docCommentAstBuilder = new DocCommentAstBuilder();
            return (DocComment)docCommentAstBuilder.visit(tree);
        }
        catch (Exception e)
        {
            ZserioToolPrinter.printError("Doc comment parsing failed: " + e.getMessage() + "!");
            return null;
        }
    }

    private BufferedTokenStream currentTokenStream = null;
    private final Set<Token> currentUsedComments = new HashSet<Token>();
}