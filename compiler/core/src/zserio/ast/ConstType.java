package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.tools.HashUtil;

/**
 * AST node for Constant types.
 *
 * Constant types are Zserio types as well.
 */
public class ConstType extends AstNodeWithDoc implements ZserioType, Comparable<ConstType>
{
    /**
     * Constructor.
     *
     * @param token           ANTLR4 token to localize AST node in the sources.
     * @param pkg             Package to which belongs the constant type.
     * @param constType       Zserio type of the constant.
     * @param name            Name of the constant type.
     * @param valueExpression Value expression associated to the constant type.
     * @param docComment      Documentation comment belonging to this node.
     */
    public ConstType(Token token, Package pkg, ZserioType constType, String name, Expression valueExpression,
            DocComment docComment)
    {
        super(token, docComment);

        this.pkg = pkg;
        this.constType = constType;
        this.name = name;
        this.valueExpression = valueExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitConstType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        constType.accept(visitor);
        valueExpression.accept(visitor);

        super.visitChildren(visitor);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public int compareTo(ConstType other)
    {
        return getName().compareTo(other.getName());
    }

    @Override
    public boolean equals(Object other)
    {
        if ( !(other instanceof ConstType) )
            return false;

        return (this == other) || compareTo((ConstType)other) == 0;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, getName());
        return hash;
    }

    /**
     * Gets unresolved Zserio type.
     *
     * @return Unresolved Zserio type.
     */
    public ZserioType getConstType()
    {
        return constType;
    }

    /**
     * Gets expression which represents constant value.
     *
     * @return Constant value expression.
     */
    public Expression getValueExpression()
    {
        return valueExpression;
    }

    /**
     * Checks the constant type.
     */
    void check()
    {
        // check base type
        final ZserioType resolvedTypeReference = TypeReference.resolveType(constType);
        final ZserioType baseType = TypeReference.resolveBaseType(resolvedTypeReference);
        if (!ZserioTypeUtil.isBuiltIn(baseType) && !(baseType instanceof EnumType))
            throw new ParserException(this, "Constants can be defined only for built-in types and enums!");

        // check expression type
        ExpressionUtil.checkExpressionType(valueExpression, baseType);

        // check integer constant range
        ExpressionUtil.checkIntegerExpressionRange(valueExpression, baseType, name);
    }

    private final Package pkg;
    private final ZserioType constType;
    private final String name;
    private final Expression valueExpression;
}
