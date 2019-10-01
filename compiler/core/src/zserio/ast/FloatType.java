package zserio.ast;

import zserio.antlr.ZserioParser;

/**
 * AST node for Float types.
 *
 * Float types are Zserio types as well.
 */
public class FloatType extends BuiltInType implements FixedSizeType
{
    /**
     * Constructor from AST node location, the name and the token type.
     *
     * @param location  AST node location.
     * @param name      Name of the AST node taken from grammar.
     * @param tokenType Grammar token type.
     */
    public FloatType(AstLocation location, String name, int tokenType)
    {
        super(location, name);

        switch (tokenType)
        {
        case ZserioParser.FLOAT16:
            bitSize = 16;
            break;

        case ZserioParser.FLOAT32:
            bitSize = 32;
            break;

        case ZserioParser.FLOAT64:
            bitSize = 64;
            break;

        default:
            throw new InternalError("Unexpected AST node type in FloatType!");
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitFloatType(this);
    }

    @Override
    public int getBitSize()
    {
        return bitSize;
    }

    private final int bitSize;
}
