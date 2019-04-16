package zserio.ast4;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

public class EnumType extends AstNodeBase implements ZserioScopedType
{
    public EnumType(Token locationToken, Package pkg, ZserioType enumType, String name,
            List<EnumItem> enumItems)
    {
        super(locationToken);

        this.pkg = pkg;
        this.enumType = enumType;
        this.name = name;
        this.enumItems = enumItems;

        for (EnumItem enumItem : enumItems)
            enumItem.setEnumType(this);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitEnumType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        enumType.accept(visitor);
        for (EnumItem enumItem : enumItems)
            enumItem.accept(visitor);
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
     * Evaluates all enumeration item value expressions of the enumeration type.
     *
     * This method is called from code Expression Evaluator.
     *
     * This method can be called directly from Expression.evaluate() method if some expression refers to
     * enumeration item before definition of this item.
     *
     * This method calculates and sets value to all enumeration items.
     *
     * @throws ParserException Throws if evaluation of all enumeration item values fails.
     */
    public void evaluateValueExpressions() throws ParserException
    {
        if (!areValuesEvaluated)
        {
            // evaluate enumeration values
            BigInteger defaultEnumItemValue = BigInteger.ZERO;
            for (EnumItem enumItem : enumItems)
            {
                enumItem.evaluateValueExpression(defaultEnumItemValue);
                defaultEnumItemValue = enumItem.getValue().add(BigInteger.ONE);
            }

            areValuesEvaluated = true;
        }
    }

    /**
     * Gets all enumeration items which belong to the enumeration type.
     *
     * @return List of all enumeration items.
     */
    public List<EnumItem> getItems()
    {
        return Collections.unmodifiableList(enumItems);
    }

    /**
     * Gets unresolved enumeration Zserio type.
     *
     * @return Unresolved enumeration Zserio type.
     */
    public ZserioType getEnumType()
    {
        return enumType;
    }

    /**
     * Gets enumeration integer type.
     *
     * @return Enumeration integer type.
     */
    public IntegerType getIntegerBaseType()
    {
        return integerBaseType;
    }

    /**
     * Gets documentation comment associated to this enumeration type.
     *
     * @return Documentation comment token associated to this enumeration type.
     */
    /* TODO
    public DocCommentToken getDocComment()
    {
        return null;
    }*/

    @Override
    protected void check() throws ParserException
    {
        // fill resolved enumeration type
        final ZserioType baseType = TypeReference.resolveBaseType(enumType);
        if (!(baseType instanceof IntegerType))
            throw new ParserException(this, "Enumeration '" + this.getName() + "' has forbidden type " +
                    baseType.getName() + "!");
        integerBaseType = (IntegerType)baseType;

        // check enumeration items
        checkEnumerationItems();
    }

    private void checkEnumerationItems() throws ParserException
    {
        final Set<BigInteger> enumItemValues = new HashSet<BigInteger>();
        for (EnumItem enumItem : enumItems)
        {
            if (enumItem.getValueExpression() != null)
                ExpressionUtil.checkExpressionType(enumItem.getValueExpression(), getIntegerBaseType());

            // check if enumeration item value is not duplicated
            final BigInteger enumItemValue = enumItem.getValue();
            if ( !enumItemValues.add(enumItemValue) )
            {
                // enumeration item value is duplicated
                throw new ParserException(enumItem.getValueExpression(), "Enumeration item '" +
                        enumItem.getName() + "' has duplicated value (" + enumItemValue + ")!");
            }

            // check enumeration item values boundaries
            final BigInteger lowerBound = integerBaseType.getLowerBound();
            final BigInteger upperBound = integerBaseType.getUpperBound();
            if (enumItemValue.compareTo(lowerBound) < 0 || enumItemValue.compareTo(upperBound) > 0)
                throw new ParserException(enumItem.getValueExpression(), "Enumeration item '" +
                        enumItem.getName() + "' has value (" + enumItemValue + ") out of range <" +
                        lowerBound + "," + upperBound + ">!");
        }
    }

    private final Scope scope = new Scope(this);
    private final Package pkg;
    private final String name;
    private final ZserioType enumType;
    private final List<EnumItem> enumItems;

    private boolean areValuesEvaluated = false;
    private IntegerType integerBaseType = null;
}
