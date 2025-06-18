package zserio.ast;

import java.util.List;

import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for array type instantiation.
 */
public final class ArrayInstantiation extends TypeInstantiation
{
    /**
     * Constructor.
     *
     * @param location                 AST node location.
     * @param typeReference            Reference to the instantiated type definition.
     * @param elementTypeInstantiation Element type instantiation.
     * @param isPacked                 Whether this array is packed.
     * @param isImplicit               Whether this is an implicit array.
     * @param lengthExpression         Array length expression.
     */
    public ArrayInstantiation(AstLocation location, TypeReference typeReference,
            TypeInstantiation elementTypeInstantiation, boolean isPacked, boolean isImplicit,
            Expression lengthExpression)
    {
        super(location, typeReference);

        this.elementTypeInstantiation = elementTypeInstantiation;
        this.isPacked = isPacked;
        this.isImplicit = isImplicit;
        this.lengthExpression = lengthExpression;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        elementTypeInstantiation.accept(visitor);
        if (lengthExpression != null)
            lengthExpression.accept(visitor);
    }

    /**
     * Gets type instantiation for array elements.
     *
     * @return Type instantiation.
     */
    public TypeInstantiation getElementTypeInstantiation()
    {
        return elementTypeInstantiation;
    }

    /**
     * Gets whether the array is a packed array.
     *
     * Note that packed arrays can be defined only for integral types or for compounds which contain
     * packable fields.
     *
     * @return True if the array is packed, false otherwise.
     */
    public boolean isPacked()
    {
        return isPacked;
    }

    /**
     * Gets whether the array is an implicit array.
     *
     * Note that implicit arrays have no length expression.
     *
     * @return True if the array is implicit, false otherwise.
     */
    public boolean isImplicit()
    {
        return isImplicit;
    }

    /**
     * Gets length expression.
     *
     * @return Array length expression or null when this is an implicit or auto array.
     */
    public Expression getLengthExpression()
    {
        return lengthExpression;
    }

    /**
     * Gets flag which indicates if the array is packable.
     *
     * @return True if the array is packable.
     */
    public boolean isPackable()
    {
        if (isImplicit())
            return false;

        final ZserioType elementBaseType = getElementTypeInstantiation().getBaseType();

        return isTypePackable(elementBaseType);
    }

    @Override
    public ArrayType getBaseType()
    {
        return (ArrayType)super.getBaseType();
    }

    @Override
    ArrayInstantiation instantiateImpl(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments, TypeReference instantiatedTypeReference)
    {
        final TypeInstantiation instantiatedElementTypeInstantiation =
                elementTypeInstantiation.instantiate(templateParameters, templateArguments);
        final Expression instantiatedLengthExpression = getLengthExpression() == null
                ? null
                : getLengthExpression().instantiate(templateParameters, templateArguments);

        return new ArrayInstantiation(getLocation(), instantiatedTypeReference,
                instantiatedElementTypeInstantiation, isPacked, isImplicit, instantiatedLengthExpression);
    }

    @Override
    void resolve()
    {
        if (!(super.getBaseType() instanceof ArrayType))
        {
            throw new ParserException(getTypeReference(),
                    "Referenced type '" + ZserioTypeUtil.getReferencedFullName(getTypeReference()) +
                            "' is not an array type!");
        }
    }

    @Override
    void evaluate()
    {
        if (!isEvaluated)
        {
            // expression needs BigInteger but Zserio type must be integer => cast expression to long
            if (lengthExpression != null && lengthExpression.needsBigInteger())
                lengthExpression.setNeedsBigIntegerCastingNative();

            isEvaluated = true;
        }
    }

    @Override
    void check(WarningsConfig warningsConfig, ZserioTemplatableType currentTemplateInstantiation)
    {
        checkPackedArrayElementType(warningsConfig, currentTemplateInstantiation);

        if (!checkImplicitArrayElementType())
        {
            throw new ParserException(elementTypeInstantiation,
                    "Implicit arrays are allowed only for types which have fixed size rounded to bytes!");
        }

        // check length expression
        if (lengthExpression != null)
        {
            ExpressionUtil.checkOffsetFields(lengthExpression);

            if (lengthExpression.getExprType() != Expression.ExpressionType.INTEGER)
            {
                throw new ParserException(
                        lengthExpression, "Invalid length expression for array. Length must be integer!");
            }
        }
    }

    /**
     * Check whether the base type is packable.
     *
     * @param baseType Base type to check.
     *
     * @return True when the base type is packable, false otherwise.
     */
    static boolean isTypePackable(ZserioType baseType)
    {
        // compound is packable if it contains at least one packable field
        if (baseType instanceof CompoundType)
            return ((CompoundType)baseType).isPackable();

        return isSimpleTypePackable(baseType);
    }

    private static boolean isSimpleTypePackable(ZserioType baseType)
    {
        return baseType instanceof IntegerType || baseType instanceof EnumType ||
                baseType instanceof BitmaskType || baseType instanceof TemplateParameter;
    }

    private void checkPackedArrayElementType(
            WarningsConfig warningsConfig, ZserioTemplatableType currentTemplateInstantiation)
    {
        if (isPacked)
        {
            final ZserioType elementBaseType = getElementTypeInstantiation().getBaseType();

            if (elementBaseType instanceof CompoundType)
            {
                final CompoundType elementCompoundType = (CompoundType)elementBaseType;
                if (!elementCompoundType.isPackable())
                {
                    // in case of empty structures, we are not able to check correctness => such warning should
                    // be enabled explicitly by command line
                    if (!elementCompoundType.getFields().isEmpty())
                    {
                        printUnpackableWarning(warningsConfig, currentTemplateInstantiation,
                                "Keyword 'packed' doesn't have any effect. "
                                        + "'" + elementCompoundType.getName() +
                                        "' doesn't contain any packable field.",
                                WarningsConfig.UNPACKABLE_ARRAY);
                    }
                }
                else if (elementCompoundType instanceof UnionType && !elementCompoundType.hasPackableField())
                {
                    printUnpackableWarning(warningsConfig, currentTemplateInstantiation,
                            "Union '" + elementCompoundType.getName() + "' doesn't contain any packable field.",
                            WarningsConfig.UNPACKABLE_UNION);
                }
            }
            else if (!(isSimpleTypePackable(elementBaseType)))
            {
                printUnpackableWarning(warningsConfig, currentTemplateInstantiation,
                        "Keyword 'packed' doesn't have any effect. "
                                + "'" + elementBaseType.getName() + "' is not packable element type.",
                        WarningsConfig.UNPACKABLE_ARRAY);
            }
        }
    }

    private void printUnpackableWarning(WarningsConfig warningsConfig,
            ZserioTemplatableType currentTemplateInstantiation, String message, String warningSpecifier)
    {
        if (currentTemplateInstantiation != null && warningsConfig.isEnabled(WarningsConfig.UNPACKABLE_ARRAY))
        {
            for (TypeReference instantiationReference :
                    currentTemplateInstantiation.getReversedInstantiationReferenceStack())
            {
                ZserioToolPrinter.printWarning(instantiationReference.getLocation(),
                        "    In instantiation of '" + instantiationReference.getReferencedTypeName() +
                                "' required from here");
            }
        }
        ZserioToolPrinter.printWarning(
                getElementTypeInstantiation(), message, warningsConfig, warningSpecifier);
    }

    private boolean checkImplicitArrayElementType()
    {
        if (!isImplicit)
            return true;

        final ZserioType elementBaseType = getElementTypeInstantiation().getBaseType();
        if (elementBaseType instanceof FixedSizeType)
        {
            final int bitSize = ((FixedSizeType)elementBaseType).getBitSize();
            if ((bitSize % 8) == 0)
                return true;
        }

        return false;
    }

    private final TypeInstantiation elementTypeInstantiation;
    private final boolean isPacked;
    private final boolean isImplicit;
    private final Expression lengthExpression;

    private boolean isEvaluated = false;
}
