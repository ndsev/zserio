package zserio.extension.python;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.IntegerType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.ExternType;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.NativeArrayType;
import zserio.extension.python.types.NativeBuiltinType;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for compound fields, used from various template data.
 */
public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(TemplateDataContext context, CompoundType parentType, Field field,
            ImportCollector importCollector) throws ZserioExtensionException
    {
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();

        name = field.getName();
        snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);

        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final PythonNativeType nativeType = pythonNativeMapper.getPythonType(fieldTypeInstantiation);
        importCollector.importType(nativeType);
        pythonTypeName = PythonFullNameFormatter.getFullName(nativeType);

        propertyName = AccessorNameFormatter.getPropertyName(field);

        final ExpressionFormatter pythonExpressionFormatter =
                context.getPythonExpressionFormatter(importCollector);
        rangeCheck = createRangeCheck(fieldTypeInstantiation, withRangeCheckCode, pythonExpressionFormatter);
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
        optional = createOptional(field, fieldBaseType, parentType, pythonExpressionFormatter);

        alignmentValue = createAlignmentValue(field, pythonExpressionFormatter);
        initializer = createInitializer(field, pythonExpressionFormatter);
        constraint = createConstraint(field, pythonExpressionFormatter);

        usesChoiceMember = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isBuiltinType = !(fieldTypeInstantiation instanceof ArrayInstantiation) &&
                (pythonNativeMapper.getPythonType(fieldBaseType) instanceof NativeBuiltinType);
        isExternType = fieldBaseType instanceof ExternType;

        arrayTraits = new ArrayTraitsTemplateData(nativeType.getArrayTraits());
        bitSize = new BitSize(fieldTypeInstantiation, pythonExpressionFormatter);
        offset = createOffset(field, pythonExpressionFormatter);
        array = createArray(nativeType, fieldTypeInstantiation, parentType, pythonNativeMapper,
                pythonExpressionFormatter, importCollector);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(
                fieldTypeInstantiation, pythonExpressionFormatter);
        compound = createCompound(pythonExpressionFormatter, fieldTypeInstantiation);
    }

    public String getName()
    {
        return name;
    }

    public String getSnakeCaseName()
    {
        return snakeCaseName;
    }

    public String getPythonTypeName()
    {
        return pythonTypeName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public RangeCheck getRangeCheck()
    {
        return rangeCheck;
    }

    public Optional getOptional()
    {
        return optional;
    }

    public String getAlignmentValue()
    {
        return alignmentValue;
    }

    public String getInitializer()
    {
        return initializer;
    }

    public String getConstraint()
    {
        return constraint;
    }

    public boolean getUsesChoiceMember()
    {
        return usesChoiceMember;
    }

    public boolean getIsBuiltinType()
    {
        return isBuiltinType;
    }

    public boolean getIsExternType()
    {
        return isExternType;
    }

    public ArrayTraitsTemplateData getArrayTraits()
    {
        return arrayTraits;
    }

    public BitSize getBitSize()
    {
        return bitSize;
    }

    public Offset getOffset()
    {
        return offset;
    }

    public Array getArray()
    {
        return array;
    }

    public RuntimeFunctionTemplateData getRuntimeFunction()
    {
        return runtimeFunction;
    }

    public Compound getCompound()
    {
        return compound;
    }

    public static class RangeCheck
    {
        public RangeCheck(BitFieldWithExpression bitFieldWithExpression, String lowerBound, String upperBound)
        {
            this.bitFieldWithExpression = bitFieldWithExpression;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public BitFieldWithExpression getBitFieldWithExpression()
        {
            return bitFieldWithExpression;
        }

        public String getLowerBound()
        {
            return lowerBound;
        }

        public String getUpperBound()
        {
            return upperBound;
        }

        private final BitFieldWithExpression bitFieldWithExpression;
        private final String lowerBound;
        private final String upperBound;
    }

    public static class BitFieldWithExpression
    {
        public BitFieldWithExpression(DynamicBitFieldInstantiation dynamicBitFieldInstantiation,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            lengthExpression = pythonExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());
            isSigned = dynamicBitFieldInstantiation.getBaseType().isSigned();
        }

        public String getLengthExpression()
        {
            return lengthExpression;
        }

        public boolean getIsSigned()
        {
            return isSigned;
        }

        private final String lengthExpression;
        private final boolean isSigned;
    }

    public static class Optional
    {
        public Optional(Field field, ExpressionFormatter pythonExpressionFormatter,
                boolean isRecursive) throws ZserioExtensionException
        {
            final String indicatorName = AccessorNameFormatter.getIndicatorName(field);
            final Expression optionalClauseExpression = field.getOptionalClauseExpr();
            clause = (optionalClauseExpression == null) ? null :
                pythonExpressionFormatter.formatGetter(optionalClauseExpression);

            this.indicatorName = indicatorName;
            this.isRecursive = isRecursive;
        }

        public String getClause()
        {
            return clause;
        }

        public String getIndicatorName()
        {
            return indicatorName;
        }

        public boolean getIsRecursive()
        {
            return isRecursive;
        }

        private final String clause;
        private final String indicatorName;
        private final boolean isRecursive;
    }

    public static class BitSize
    {
        public BitSize(TypeInstantiation typeInstantiation, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioExtensionException
        {
            value = createValue(typeInstantiation, pythonExpressionFormatter);
            runtimeFunction = (value != null) ? null :
                PythonRuntimeFunctionDataCreator.createData(typeInstantiation, pythonExpressionFormatter);
        }

        public String getValue()
        {
            return value;
        }

        public RuntimeFunctionTemplateData getRuntimeFunction()
        {
            return runtimeFunction;
        }

        private static String createValue(TypeInstantiation typeInstantiation,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            String bitSizeOfValue = null;
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                bitSizeOfValue = PythonLiteralFormatter.formatDecimalLiteral(
                        ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            }
            else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)typeInstantiation;
                bitSizeOfValue = pythonExpressionFormatter.formatGetter(
                        dynamicBitFieldInstantiation.getLengthExpression());
            }

            return bitSizeOfValue;
        }

        private final String value;
        private final RuntimeFunctionTemplateData runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioExtensionException
        {
            getter = pythonExpressionFormatter.formatGetter(offsetExpression);
            setter = pythonExpressionFormatter.formatSetter(offsetExpression);
            containsIndex = offsetExpression.containsIndex();
        }

        public String getGetter()
        {
            return getter;
        }

        public String getSetter()
        {
            return setter;
        }

        public boolean getContainsIndex()
        {
            return containsIndex;
        }

        private final String getter;
        private final String setter;
        private final boolean containsIndex;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayInstantiation arrayInstantiation, ZserioType parentType,
                PythonNativeMapper pythonNativeMapper, ExpressionFormatter pythonExpressionFormatter,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            packedTraitsName = nativeType.getPackedArrayTraitsName();
            traits = new ArrayTraitsTemplateData(nativeType.getArrayTraits());

            isImplicit = arrayInstantiation.isImplicit();
            isPacked = arrayInstantiation.isPacked();
            length = createLength(arrayInstantiation, pythonExpressionFormatter);

            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            final PythonNativeType elementNativeType =
                    pythonNativeMapper.getPythonType(elementTypeInstantiation);
            importCollector.importType(elementNativeType);
            elementPythonTypeName = PythonFullNameFormatter.getFullName(elementNativeType);
            elementIsRecursive = (elementTypeInstantiation.getBaseType() == parentType);
            elementBitSize = new BitSize(elementTypeInstantiation, pythonExpressionFormatter);
            elementCompound = createCompound(pythonExpressionFormatter, elementTypeInstantiation);
        }

        public String getPackedTraitsName()
        {
            return packedTraitsName;
        }

        public ArrayTraitsTemplateData getTraits()
        {
            return traits;
        }

        public boolean getIsImplicit()
        {
            return isImplicit;
        }

        public boolean getIsPacked()
        {
            return isPacked;
        }

        public String getLength()
        {
            return length;
        }

        public String getElementPythonTypeName()
        {
            return elementPythonTypeName;
        }

        public boolean getElementIsRecursive()
        {
            return elementIsRecursive;
        }

        public BitSize getElementBitSize()
        {
            return elementBitSize;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return pythonExpressionFormatter.formatGetter(lengthExpression);
        }

        private final String packedTraitsName;
        private final ArrayTraitsTemplateData traits;
        private final boolean isImplicit;
        private final boolean isPacked;
        private final String length;
        private final String elementPythonTypeName;
        private final boolean elementIsRecursive;
        private final BitSize elementBitSize;
        private final Compound elementCompound;
    }

    public static class Compound
    {
        public Compound()
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
        }

        public Compound(ExpressionFormatter pythonExpressionFormatter,
                ParameterizedTypeInstantiation parameterizedInstantiation) throws ZserioExtensionException
        {
            final List<InstantiatedParameter> parameters = parameterizedInstantiation.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
                instantiatedParameters.add(new InstantiatedParameterData(pythonExpressionFormatter, parameter));
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public static class InstantiatedParameterData
        {
            public InstantiatedParameterData(ExpressionFormatter pythonExpressionFormatter,
                    InstantiatedParameter instantiatedParameter) throws ZserioExtensionException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                expression = pythonExpressionFormatter.formatGetter(argumentExpression);
                containsIndex = argumentExpression.containsIndex();
            }

            public String getExpression()
            {
                return expression;
            }

            public boolean getContainsIndex()
            {
                return containsIndex;
            }

            private final String expression;
            private final boolean containsIndex;
        }

        final List<InstantiatedParameterData> instantiatedParameters;
    }

    private static RangeCheck createRangeCheck(TypeInstantiation typeInstantiation, boolean withRangeCheckCode,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        // don't do range check for non-integer type
        if (!withRangeCheckCode || !(baseType instanceof IntegerType))
            return null;

        final IntegerType integerType = (IntegerType)baseType;
        final BitFieldWithExpression bitFieldWithExpression = createBitFieldWithExpression(typeInstantiation,
                pythonExpressionFormatter);

        final BigInteger zserioLowerBound = integerType.getLowerBound(typeInstantiation);
        final String lowerBound = zserioLowerBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioLowerBound) : null;

        final BigInteger zserioUpperBound = integerType.getUpperBound(typeInstantiation);
        final String upperBound = zserioUpperBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioUpperBound) : null;

        return new RangeCheck(bitFieldWithExpression, lowerBound, upperBound);
    }

    private static BitFieldWithExpression createBitFieldWithExpression(TypeInstantiation typeInstantiation,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof DynamicBitFieldInstantiation))
            return null;

        return new BitFieldWithExpression(
                (DynamicBitFieldInstantiation)typeInstantiation, pythonExpressionFormatter);
    }

    private static Optional createOptional(Field field, ZserioType baseFieldType, CompoundType parentType,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        if (!field.isOptional())
            return null;

        final boolean isRecursive = (baseFieldType == parentType);

        return new Optional(field, pythonExpressionFormatter, isRecursive);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createConstraint(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(constraintExpression);
    }

    private static Offset createOffset(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, pythonExpressionFormatter);
    }

    private static Array createArray(PythonNativeType nativeType, TypeInstantiation typeInstantiation,
            ZserioType parentType, PythonNativeMapper pythonNativeMapper,
            ExpressionFormatter pythonExpressionFormatter, ImportCollector importCollector)
                    throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof ArrayInstantiation))
            return null;

        if (!(nativeType instanceof NativeArrayType))
        {
            throw new ZserioExtensionException("Inconsistent base type '" + typeInstantiation.getClass().getName() +
                    "' and native type '" + nativeType.getClass().getName() + "'!");
        }

        return new Array((NativeArrayType)nativeType, (ArrayInstantiation)typeInstantiation, parentType,
                pythonNativeMapper, pythonExpressionFormatter, importCollector);
    }

    private static Compound createCompound(ExpressionFormatter pythonExpressionFormatter,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            return new Compound(pythonExpressionFormatter, (ParameterizedTypeInstantiation)typeInstantiation);
        else if (typeInstantiation.getBaseType() instanceof CompoundType)
            return new Compound();
        else
            return null;
    }

    private final String name;
    private final String snakeCaseName;
    private final String pythonTypeName;
    private final String propertyName;

    private final RangeCheck rangeCheck;
    private final Optional optional;

    private final String alignmentValue;
    private final String initializer;
    private final String constraint;

    private final boolean usesChoiceMember;
    private final boolean isBuiltinType;
    private final boolean isExternType;

    private final ArrayTraitsTemplateData arrayTraits;
    private final BitSize bitSize;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
}
