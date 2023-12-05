package zserio.ast;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AST node for Enumeration types.
 *
 * Enumeration types are Zserio types as well.
 */
public final class EnumType extends DocumentableAstNode implements ZserioScopedType
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param pkg               Package to which belongs the enumeration type.
     * @param typeInstantiation Type instantiation of the enumeration type.
     * @param name              Name of the enumeration type.
     * @param enumItems         List of all items which belong to the enumeration type.
     * @param docComments       List of documentation comments belonging to this node.
     */
    public EnumType(AstLocation location, Package pkg, TypeInstantiation typeInstantiation, String name,
            List<EnumItem> enumItems, List<DocComment> docComments)
    {
        super(location, docComments);

        this.pkg = pkg;
        this.typeInstantiation = typeInstantiation;
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

        typeInstantiation.accept(visitor);
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
     * Gets the enum's type instantiation.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getTypeInstantiation()
    {
        return typeInstantiation;
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
            final ZserioType baseType = typeInstantiation.getBaseType();
            if (!(baseType instanceof IntegerType))
                throw new ParserException(this, "Enumeration '" + this.getName() + "' has forbidden type " +
                        baseType.getName() + "!");

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
        final Map<BigInteger, EnumItem> intValues = new HashMap<BigInteger, EnumItem>();
        for (EnumItem enumItem : enumItems)
        {
            if (enumItem.getValueExpression() != null)
                ExpressionUtil.checkExpressionType(enumItem.getValueExpression(), typeInstantiation);

            // check if enumeration item value is not duplicated
            final BigInteger intValue = enumItem.getValue();
            final EnumItem prevItem = intValues.put(intValue, enumItem);
            if (prevItem != null)
            {
                // enumeration item value is duplicated
                final ParserStackedException stackedException = new ParserStackedException(
                        getValueLocation(enumItem), "Enumeration item '" + enumItem.getName() +
                        "' has duplicated value (" + intValue + ")!");
                stackedException.pushMessage(getValueLocation(prevItem), "    First defined here");
                throw stackedException;
            }

            // check enumeration item values boundaries
            final IntegerType integerBaseType = (IntegerType)typeInstantiation.getBaseType();
            final BigInteger lowerBound = integerBaseType.getLowerBound(typeInstantiation);
            final BigInteger upperBound = integerBaseType.getUpperBound(typeInstantiation);
            if (intValue.compareTo(lowerBound) < 0 || intValue.compareTo(upperBound) > 0)
            {
                throw new ParserException(getValueLocation(enumItem),
                        "Enumeration item '" + enumItem.getName() + "' has value (" + intValue +
                        ") out of range <" + lowerBound + "," + upperBound + ">!");
            }
        }
    }

    private AstLocation getValueLocation(EnumItem enumItem)
    {
        return enumItem.getValueExpression() != null ?
                enumItem.getValueExpression().getLocation() : enumItem.getLocation();
    }

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final TypeInstantiation typeInstantiation;
    private final String name;
    private final List<EnumItem> enumItems;

    private boolean isEvaluated = false;
}
