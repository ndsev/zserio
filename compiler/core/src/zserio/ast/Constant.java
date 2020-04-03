package zserio.ast;

import zserio.tools.HashUtil;

/**
 * AST node for Constants.
 */
public class Constant extends DocumentableAstNode implements Comparable<Constant>
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param pkg               Package to which belongs the constant type.
     * @param typeInstantiation Type instantiation of the constant.
     * @param name              Name of the constant type.
     * @param valueExpression   Value expression associated to the constant type.
     * @param docComment        Documentation comment belonging to this node.
     */
    public Constant(AstLocation location, Package pkg, TypeInstantiation typeInstantiation, String name,
            Expression valueExpression, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.typeInstantiation = typeInstantiation;
        this.name = name;
        this.valueExpression = valueExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitConstant(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        typeInstantiation.accept(visitor);
        valueExpression.accept(visitor);
    }

    @Override
    public int compareTo(Constant other)
    {
        return getName().compareTo(other.getName());
    }

    @Override
    public boolean equals(Object other)
    {
        if ( !(other instanceof Constant) )
            return false;

        return (this == other) || compareTo((Constant)other) == 0;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, getName());
        return hash;
    }

    /**
     * Gets the package in which this constant is defined.
     *
     * @return The package in which this constant is defined.
     */
    public Package getPackage()
    {
        return pkg;
    }

    /**
     * Gets the name of the constant.
     *
     * @return Constant name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the constant's type instantiation.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getTypeInstantiation()
    {
        return typeInstantiation;
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
     * Checks the constant.
     */
    void check()
    {
        // check base type
        final ZserioType baseType = typeInstantiation.getBaseType();
        if (!(baseType instanceof BuiltInType) &&
                !(baseType instanceof EnumType) &&
                !(baseType instanceof BitmaskType))
        {
            throw new ParserException(this,
                    "Constants can be defined only for built-in types, enums or bitmasks!");
        }

        // check expression type
        ExpressionUtil.checkExpressionType(valueExpression, typeInstantiation);

        // check integer constant range
        ExpressionUtil.checkIntegerExpressionRange(valueExpression, typeInstantiation, name);

        // check constant name
        final ZserioType definedType = pkg.getVisibleType(this, PackageName.EMPTY, getName());
        if (definedType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(getLocation(),
                    "'" + getName() + "' is a defined type in this package!");
            stackedException.pushMessage(definedType.getLocation(), "    First defined here");
            throw stackedException;
        }
    }

    private final Package pkg;
    private final TypeInstantiation typeInstantiation;
    private final String name;
    private final Expression valueExpression;
}
