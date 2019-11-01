package zserio.ast;

import zserio.tools.HashUtil;

/**
 * AST node for Constant types.
 *
 * Constant types are Zserio types as well.
 */
public class ConstType extends DocumentableAstNode implements ZserioType, Comparable<ConstType>
{
    /**
     * Constructor.
     *
     * @param location               AST node location.
     * @param pkg                    Package to which belongs the constant type.
     * @param constTypeInstantiation Type instantiation of the constant.
     * @param name                   Name of the constant type.
     * @param valueExpression        Value expression associated to the constant type.
     * @param docComment             Documentation comment belonging to this node.
     */
    public ConstType(AstLocation location, Package pkg, TypeInstantiation constTypeInstantiation, String name,
            Expression valueExpression, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.constTypeInstantiation = constTypeInstantiation;
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
        super.visitChildren(visitor);

        constTypeInstantiation.accept(visitor);
        valueExpression.accept(visitor);
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
     * Gets reference to the type of this constant.
     *
     * @return Type reference.
     */
    public TypeReference getTypeReference()
    {
        return constTypeInstantiation.getTypeReference();
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
        final ZserioType baseType = constTypeInstantiation.getTypeReference().getBaseTypeReference().getType();
        if (!(baseType instanceof BuiltInType) && !(baseType instanceof EnumType))
            throw new ParserException(this, "Constants can be defined only for built-in types and enums!");

        // check expression type
        ExpressionUtil.checkExpressionType(valueExpression, baseType);

        // check integer constant range
        ExpressionUtil.checkIntegerExpressionRange(valueExpression, baseType, name);
    }

    private final Package pkg;
    private final TypeInstantiation constTypeInstantiation;
    private final String name;
    private final Expression valueExpression;
}
