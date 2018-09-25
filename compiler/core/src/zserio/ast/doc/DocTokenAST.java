package zserio.ast.doc;

import zserio.antlr.DocCommentParser;
import zserio.antlr.util.ParserException;
import zserio.antlr.util.TokenTypeDescriptor;
import zserio.antlr.util.BaseTokenAST;
import zserio.ast.ZserioType;

/**
 * The base class for all AST token used by documentation comment grammar.
 */
public class DocTokenAST extends BaseTokenAST
{
    /**
     * Empty constructor.
     */
    public DocTokenAST()
    {
        super(tokenTypeDescriptor);
    }

    /**
     * Checks token integrity together with all its children.
     *
     * @param owner Zserio type to which belongs whole documentation comment.
     *
     * @throws ParserException Throws if integrity checking fails.
     */
    public void checkAll(ZserioType owner) throws ParserException
    {
        for (DocTokenAST child = (DocTokenAST)getFirstChild(); child != null;
                child = (DocTokenAST)child.getNextSibling())
            child.checkAll(owner);

        check(owner);
    }

    /**
     * Checks token integrity.
     *
     * This method should be implemented by inherited class.
     *
     * @param owner Zserio type to which belongs whole documentation comment.
     *
     * @throws ParserException Throws if integrity checking fails.
     */
    protected void check(ZserioType owner) throws ParserException
    {
    }

    private static final long serialVersionUID = -1L;
    private static final TokenTypeDescriptor tokenTypeDescriptor =
            new TokenTypeDescriptor(DocCommentParser.class);
}
