package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.ZserioType;

/**
 * AST node for a parameter defined in compound types.
 */
public class Parameter extends TokenAST
{
    public ZserioType getParameterType()
    {
        return parameterType;
    }

    public String getName()
    {
        return name;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            name = child.getText();
            break;

        default:
            if (parameterType != null || !(child instanceof ZserioType))
                return false;
            parameterType = (ZserioType)child;
            break;
        }

        return true;
    }

    private static final long serialVersionUID = 8311434055636275517L;

    private ZserioType  parameterType;
    private String          name;
}
