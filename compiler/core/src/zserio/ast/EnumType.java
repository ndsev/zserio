package zserio.ast;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AST node for Enumeration types.
 *
 * Enumeration types are Zserio types as well.
 */
public class EnumType extends DocumentableAstNode implements ZserioScopedType
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param pkg               Package to which belongs the enumeration type.
     * @param enumTypeReference Type reference to the enumeration type.
     * @param name              Name of the enumeration type.
     * @param enumItems         List of all items which belong to the enumeration type.
     * @param docComment        Documentation comment belonging to this node.
     */
    public EnumType(AstLocation location, Package pkg, TypeReference enumTypeReference, String name,
            List<EnumItem> enumItems, DocComment docComment)
    {
        super(location, docComment);

        this.pkg = pkg;
        this.enumTypeReference = enumTypeReference;
        this.name = name;
        this.enumItems = enumItems;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitEnumType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        enumTypeReference.accept(visitor);
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
        return enumTypeReference.getType();
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
     * Evaluates all enumeration item value expressions of the enumeration type.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to enumeration
     * item before definition of this item. Therefore 'isEvaluated' check is necessary.
     *
     * This method calculates and sets value to all enumeration items.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            // fill resolved enumeration type
            final ZserioType baseType = enumTypeReference.getBaseTypeReference().getType();
            if (!(baseType instanceof IntegerType))
                throw new ParserException(this, "Enumeration '" + this.getName() + "' has forbidden type " +
                        baseType.getName() + "!");
            integerBaseType = (IntegerType)baseType;

            // evaluate enumeration values
            BigInteger defaultEnumItemValue = BigInteger.ZERO;
            for (EnumItem enumItem : enumItems)
            {
                if (enumItem.getValueExpression() == null)
                    enumItem.setValue(defaultEnumItemValue);

                defaultEnumItemValue = enumItem.getValue().add(BigInteger.ONE);
            }

            isEvaluated = true;
        }
    }

    /**
     * Checks the enumeration type.
     */
    void check()
    {
        // check enumeration items
        checkEnumerationItems();
    }

    private void checkEnumerationItems()
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
    private final TypeReference enumTypeReference;
    private final String name;
    private final List<EnumItem> enumItems;

    private boolean isEvaluated = false;
    private IntegerType integerBaseType = null;
}
