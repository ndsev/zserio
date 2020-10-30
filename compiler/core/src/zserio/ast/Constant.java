package zserio.ast;

import java.util.List;

/**
 * AST node for Constants.
 */
public class Constant extends DocumentableAstNode implements PackageSymbol
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param pkg               Package to which belongs the constant type.
     * @param typeInstantiation Type instantiation of the constant.
     * @param name              Name of the constant type.
     * @param valueExpression   Value expression associated to the constant type.
     * @param docComments       List of documentation comments belonging to this node.
     */
    public Constant(AstLocation location, Package pkg, TypeInstantiation typeInstantiation, String name,
            Expression valueExpression, List<DocComment> docComments)
    {
        super(location, docComments);

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
    public Package getPackage()
    {
        return pkg;
    }

    @Override
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
    }

    private final Package pkg;
    private final TypeInstantiation typeInstantiation;
    private final String name;
    private final Expression valueExpression;
}
