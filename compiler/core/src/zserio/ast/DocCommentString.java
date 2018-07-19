package zserio.ast;

import java.io.StringReader;

import antlr.CommonHiddenStreamToken;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import antlr.TokenStreamRecognitionException;

import zserio.antlr.DocCommentLexer;
import zserio.antlr.DocCommentParser;
import zserio.antlr.util.FileNameLexerToken;
import zserio.antlr.util.ParserException;
import zserio.ast.doc.DocCommentToken;
import zserio.ast.doc.DocTokenAST;

/**
 * The representation of AST node type DOC_COMMENT.
 *
 * The AST node DOC_COMMENT represents documentation comment as a single string. The documentation comment
 * string is parsed using special grammar file DocComment.g.
 *
 * This AST node is not created automatically by ANTRL but explicitly by TokenAST class during scanning
 * of all hidden tokens.
 */
public class DocCommentString extends TokenAST
{
    /**
     * Constructor from lexer token and owner.
     *
     * @param lexerToken Lexer token to construct from.
     * @param owner      Zserio type to which belongs this documentation comment.
     */
    public DocCommentString(CommonHiddenStreamToken lexerToken, ZserioType owner)
    {
        super(lexerToken);
        this.owner = owner;
    }

    /**
     * Gets documentation comment token parsed from documentation comment string.
     *
     * @return Documentation comment token parsed from documentation comment string.
     */
    public DocCommentToken getDocCommentToken()
    {
        return docCommentToken;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        final int commentLexerTokenLine = getLine();
        final int baseLineNumber = (commentLexerTokenLine > 0) ? commentLexerTokenLine - 1 : 0;
        final StringReader inputString = new StringReader(getText());
        final DocCommentLexer docLexer = new DocCommentLexerWithFileNameSupport(inputString, baseLineNumber);
        docLexer.setFilename(getFileName());
        docLexer.setTokenObjectClass(FileNameLexerToken.class.getCanonicalName());
        final DocCommentParser docParser = new DocCommentParser(docLexer);
        docParser.setASTNodeClass(DocTokenAST.class.getCanonicalName());
        try
        {
            docParser.docCommentDeclaration();
        }
        catch (RecognitionException exception)
        {
            throw new ParserException(getFileName(), exception.getLine(), exception.getColumn(),
                    exception.getMessage());
        }
        catch (TokenStreamRecognitionException exception)
        {
            if (exception.recog != null)
                throw new ParserException(getFileName(), exception.recog.getLine(), exception.recog.getColumn(),
                        exception.recog.getMessage());
            else
                throw new ParserException(getFileName(), exception.toString());
        }
        catch (TokenStreamException exception)
        {
            throw new ParserException(getFileName(), exception.toString());
        }

        docCommentToken = (DocCommentToken)docParser.getAST();
        docCommentToken.evaluateAll();
    }

    @Override
    protected void check() throws ParserException
    {
        docCommentToken.checkAll(owner);
    }

    private static class DocCommentLexerWithFileNameSupport extends DocCommentLexer
    {
        DocCommentLexerWithFileNameSupport(StringReader reader, int baseLineNumber)
        {
            super(reader);
            this.baseLineNumber = baseLineNumber;
        }

        @Override
        public int getLine()
        {
            return baseLineNumber + super.getLine();
        }

        @Override
        protected Token makeToken(int t)
        {
            final Token token = super.makeToken(t);
            token.setFilename(getFilename());
            token.setLine(baseLineNumber + token.getLine());

            return token;
        }

        private int baseLineNumber;
    }

    private static final long serialVersionUID = -1L;

    private final ZserioType    owner;
    private DocCommentToken         docCommentToken = null;
}
