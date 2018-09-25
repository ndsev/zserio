package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for auto choice types.
 *
 * Auto choice types are Zserio types as well.
 */
public class UnionType extends CompoundType
{
    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (!(child instanceof IdToken))
                return false;
            setName(child.getText());
            break;

        case ZserioParserTokenTypes.PARAM:
            if (!(child instanceof Parameter))
                return false;
            addParameter((Parameter)child);
            break;

        case ZserioParserTokenTypes.FIELD:
            if (!(child instanceof Field))
                return false;
            addField((Field)child);
            break;

        case ZserioParserTokenTypes.FUNCTION:
            if (!(child instanceof FunctionType))
                return false;
            addFunction((FunctionType)child);
            break;
        default:
            return false;
        }

        return true;
    }

    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitUnionType(this);
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluateHiddenDocComment(this);
        setDocComment(getHiddenDocComment());
    }

    @Override
    protected void check() throws ParserException
    {
        super.check();
        checkTableFields();
    }

    private static final long serialVersionUID = 6695949481469160388L;
};
