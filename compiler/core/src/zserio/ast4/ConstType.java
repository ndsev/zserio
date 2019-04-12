package zserio.ast4;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.antlr.v4.runtime.Token;

import zserio.tools.HashUtil;

/**
 * AST node for constant types.
 *
 * Constant types are Zserio types as well.
 */
public class ConstType extends AstNodeBase implements ZserioType, Comparable<ConstType>
{
    public ConstType(Token token, Package pkg, ZserioType constType, String name, Expression valueExpression)
    {
        super(token);

        this.pkg = pkg;
        this.constType = constType;
        this.name = name;
        this.valueExpression = valueExpression;
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.beginConstType(this);

        constType.walk(listener);
        valueExpression.walk(listener);

        listener.endConstType(this);
    }

    /*@Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitConstType(this);
    }*/

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
     * Gets unresolved const Zserio type.
     *
     * @return Unresolved const Zserio type.
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
     * Gets list of compound types which use this constant type.
     *
     * @return List of compound types which use this constant type.
     */
    public Iterable<CompoundType> getUsedByCompoundList()
    {
        return usedByCompoundList;
    }

    /*@Override
    protected void check() throws ParserException
    {
        // fill used type list
        final ZserioType resolvedTypeReference = TypeReference.resolveType(constType);
        if (!ZserioTypeUtil.isBuiltIn(resolvedTypeReference))
            usedTypeList.add(resolvedTypeReference);

        // add this const to 'Used-by' list for subtype type (needed by documentation emitter)
        if (resolvedTypeReference instanceof Subtype)
            ((Subtype)resolvedTypeReference).setUsedByConst(this);

        final ZserioType baseType = TypeReference.resolveBaseType(resolvedTypeReference);

        // check base type
        if (!ZserioTypeUtil.isBuiltIn(baseType) && !(baseType instanceof EnumType))
            throw new ParserException(this, "Constants can be defined only for built-in types and enums!");

        // check expression type
        ExpressionUtil.checkExpressionType(valueExpression, baseType);

        // check integer constant range
        ExpressionUtil.checkIntegerExpressionRange(valueExpression, baseType, name);
    }*/ // TODO:

    /**
     * Sets expression which uses this constant type.
     *
     * @param expression Expression to set.
     */
    /*protected void setUsedByExpression(Expression expression)
    {
        final ZserioType ownerType = expression.getScope().getOwner();
        if (ownerType != null && ownerType instanceof CompoundType)
            usedByCompoundList.add((CompoundType)ownerType);
    }*/ // TODO:

    private final Package pkg;

    private final ZserioType constType;
    private final String name;
    private final Expression valueExpression;

    private final SortedSet<CompoundType> usedByCompoundList = new TreeSet<CompoundType>();
    private final List<ZserioType> usedTypeList = new ArrayList<ZserioType>();
}
