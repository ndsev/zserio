package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * AST node for a parameter defined in compound types.
 */
public class Parameter extends AstNodeBase
{
    public Parameter(Token token, ZserioType parameterType, String name)
    {
        super(token);

        this.parameterType = parameterType;
        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitParameter(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        parameterType.accept(visitor);
    }

    /**
     * Gets parameter Zserio type.
     *
     * @return Parameter type.
     */
    public ZserioType getParameterType()
    {
        return parameterType;
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getName()
    {
        return name;
    }

    private final ZserioType parameterType;
    private final String name;
}
