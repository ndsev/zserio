package zserio.ast;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AST node for Bitmask types.
 *
 * Bitmask types are Zserio types as well.
 */
public class BitmaskType extends DocumentableAstNode implements ZserioScopedType
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param pkg               Package to which belongs the bitmask type.
     * @param typeInstantiation Type instantiation of the bitmask type.
     * @param name              Name of the bitmask type.
     * @param bitmaskValues     List of all named values which belong to the bitmask type.
     * @param docComment        Documentation comment belonging to this node.
     */
    public BitmaskType(AstLocation location, Package pkg, TypeInstantiation typeInstantiation, String name,
            List<BitmaskValue> values, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.typeInstantiation = typeInstantiation;
        this.name = name;
        this.values = values;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBitmaskType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        typeInstantiation.accept(visitor);
        for (BitmaskValue flag : values)
            flag.accept(visitor);
    }

    @Override
    public Scope getScope()
    {
        return scope;
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
     * Gets all named values which belong to the bitmask type.
     *
     * @return List of all flags.
     */
    public List<BitmaskValue> getValues()
    {
        return Collections.unmodifiableList(values);
    }

    /**
     * Gets the bitmask's type instantiation.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getTypeInstantiation()
    {
        return typeInstantiation;
    }

    /**
     * Evaluates all value expressions of the bitmask type.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to a
     * value before definition of this value. Therefore 'isEvaluated' check is necessary.
     *
     * This method calculates and sets all named values.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            // fill resolved bitmask type
            final ZserioType baseType = typeInstantiation.getBaseType();
            if (!(baseType instanceof IntegerType) || ((IntegerType)baseType).isSigned())
            {
                throw new ParserException(this, "Bitmask '" + this.getName() + "' cannot use " +
                        baseType.getName() + " type! Only unsigned integer types are allowed!");
            }

            // evaluate bitmask values
            BigInteger defaultValue = BigInteger.ONE;
            for (BitmaskValue value : values)
            {
                if (value.getValueExpression() == null)
                    value.setValue(defaultValue);

                defaultValue = getNextValue(value.getValue());
            }

            isEvaluated = true;
        }
    }

    /**
     * Checks the bitmask type.
     */
    void check()
    {
        checkBitmaskValues();
    }

    private BigInteger getNextValue(BigInteger lastValue)
    {
        int n = 0;
        while (!(lastValue = lastValue.shiftRight(1)).equals(BigInteger.ZERO))
            n++;

        return BigInteger.ONE.shiftLeft(n + 1);
    }

    private void checkBitmaskValues()
    {
        final Map<BigInteger, BitmaskValue> intValues = new HashMap<BigInteger, BitmaskValue>();
        for (BitmaskValue bitmaskValue : values)
        {
            if (bitmaskValue.getValueExpression() != null)
                ExpressionUtil.checkExpressionType(bitmaskValue.getValueExpression(), typeInstantiation);

            // check if bitmask value is not duplicated
            final BigInteger intValue = bitmaskValue.getValue();
            final BitmaskValue prevValue = intValues.put(intValue, bitmaskValue);
            if (prevValue != null)
            {
                // bitmask value is duplicated
                final ParserStackedException stackedException = new ParserStackedException(
                        getValueLocation(bitmaskValue), "Bitmask '" + bitmaskValue.getName() +
                        "' has duplicated value (" + intValue + ")!");
                stackedException.pushMessage(getValueLocation(prevValue), "    First defined here");
                throw stackedException;
            }

            final IntegerType integerBaseType = (IntegerType)typeInstantiation.getBaseType();

            // check bitmask values boundaries
            final BigInteger lowerBound = integerBaseType.getLowerBound(typeInstantiation);
            final BigInteger upperBound = integerBaseType.getUpperBound(typeInstantiation);
            if (intValue.compareTo(lowerBound) < 0 || intValue.compareTo(upperBound) > 0)
            {
                throw new ParserException(getValueLocation(bitmaskValue), "Bitmask '" + bitmaskValue.getName() +
                        "' has value (" + intValue + ") out of range <" + lowerBound + "," + upperBound + ">!");
            }
        }
    }

    private AstLocation getValueLocation(BitmaskValue bitmaskValue)
    {
        return bitmaskValue.getValueExpression() != null ?
                bitmaskValue.getValueExpression().getLocation() : bitmaskValue.getLocation();
    }

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final TypeInstantiation typeInstantiation;
    private final String name;
    private final List<BitmaskValue> values;

    private boolean isEvaluated = false;
}
