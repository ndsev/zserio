package zserio.emit.python;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.IntegerType;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeInstantiation.InstantiatedParameter;
import zserio.ast.TypeReference;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.NativeArrayType;
import zserio.emit.python.types.PythonNativeType;

public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(PythonNativeTypeMapper pythonNativeTypeMapper, boolean withRangeCheckCode,
            CompoundType parentType, Field field, ExpressionFormatter pythonExpressionFormatter,
            ImportCollector importCollector) throws ZserioEmitException
    {
        name = field.getName();

        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final TypeReference fieldTypeReference = fieldTypeInstantiation.getTypeReference();
        final ZserioType fieldBaseType = fieldTypeReference.getBaseTypeReference().getType();
        final PythonNativeType nativeType = pythonNativeTypeMapper.getPythonType(fieldTypeReference);
        importCollector.importType(nativeType);
        pythonTypeName = nativeType.getFullName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        rangeCheck = createRangeCheck(fieldBaseType, withRangeCheckCode, pythonExpressionFormatter);
        optional = createOptional(field, pythonExpressionFormatter);

        alignmentValue = createAlignmentValue(field, pythonExpressionFormatter);
        initializer = createInitializer(field, pythonExpressionFormatter);
        constraint = createConstraint(field, pythonExpressionFormatter);

        usesChoiceMember = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        bitSize = new BitSize(fieldBaseType, pythonExpressionFormatter);
        offset = createOffset(field, pythonExpressionFormatter);
        array = createArray(nativeType, fieldBaseType, pythonNativeTypeMapper, pythonExpressionFormatter,
                importCollector);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(fieldBaseType, pythonExpressionFormatter);
        compound = createCompound(pythonExpressionFormatter, fieldTypeInstantiation);
    }

    public String getName()
    {
        return name;
    }

    public String getPythonTypeName()
    {
        return pythonTypeName;
    }

    public String getGetterName()
    {
        return getterName;
    }

    public String getSetterName()
    {
        return setterName;
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
        public BitFieldWithExpression(BitFieldType bitFieldType,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
        {
            lengthExpression = createBitFieldLengthExpression(bitFieldType, pythonExpressionFormatter);
            isSigned = bitFieldType.isSigned();
        }

        public String getLengthExpression()
        {
            return lengthExpression;
        }

        public boolean getIsSigned()
        {
            return isSigned;
        }

        private static String createBitFieldLengthExpression(BitFieldType bitFieldType,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
        {
            final Expression lengthExpression = bitFieldType.getLengthExpression();
            return pythonExpressionFormatter.formatGetter(lengthExpression);
        }

        private final String lengthExpression;
        private final boolean isSigned;
    }

    public static class Optional
    {
        public Optional(Expression optionalClauseExpression, String indicatorName,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
        {
            clause = (optionalClauseExpression == null) ? null :
                pythonExpressionFormatter.formatGetter(optionalClauseExpression);
            this.indicatorName = indicatorName;
        }

        public String getClause()
        {
            return clause;
        }

        public String getIndicatorName()
        {
            return indicatorName;
        }

        private final String clause;
        private final String indicatorName;
    }

    public static class BitSize
    {
        public BitSize(ZserioType type, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
        {
            value = createValue(type, pythonExpressionFormatter);
            runtimeFunction = (value != null) ? null :
                PythonRuntimeFunctionDataCreator.createData(type, pythonExpressionFormatter);
        }

        public String getValue()
        {
            return value;
        }

        public RuntimeFunctionTemplateData getRuntimeFunction()
        {
            return runtimeFunction;
        }

        private static String createValue(ZserioType type, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
        {
            String bitSizeOfValue = null;
            if (type instanceof FixedSizeType)
            {
                bitSizeOfValue = PythonLiteralFormatter.formatDecimalLiteral(
                        ((FixedSizeType)type).getBitSize());
            }
            else if (type instanceof BitFieldType)
            {
                final BitFieldType bitFieldType = (BitFieldType)type;
                final Integer bitSize = bitFieldType.getBitSize();
                bitSizeOfValue = (bitSize != null) ? PythonLiteralFormatter.formatDecimalLiteral(bitSize) :
                    pythonExpressionFormatter.formatGetter(bitFieldType.getLengthExpression());
            }

            return bitSizeOfValue;
        }

        private final String value;
        private final RuntimeFunctionTemplateData runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
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
        public Array(NativeArrayType nativeType, ArrayType arrayType,
                PythonNativeTypeMapper pythonNativeTypeMapper, ExpressionFormatter pythonExpressionFormatter,
                ImportCollector importCollector) throws ZserioEmitException
        {
            traitsName = nativeType.getTraitsName();
            requiresElementBitSize = nativeType.getRequiresElementBitSize();
            requiresElementCreator = nativeType.getRequiresElementCreator();

            isImplicit = arrayType.isImplicit();
            length = createLength(arrayType, pythonExpressionFormatter);

            final TypeInstantiation elementTypeInstantiation = arrayType.getElementTypeInstantiation();
            final TypeReference elementTypeReference = elementTypeInstantiation.getTypeReference();
            final ZserioType elementBaseType = elementTypeReference.getBaseTypeReference().getType();
            final PythonNativeType elementNativeType =
                    pythonNativeTypeMapper.getPythonType(elementTypeReference);
            importCollector.importType(elementNativeType);
            elementPythonTypeName = elementNativeType.getFullName();
            elementBitSize = new BitSize(elementBaseType, pythonExpressionFormatter);
            elementCompound = createCompound(pythonExpressionFormatter, elementTypeInstantiation);
        }

        public String getTraitsName()
        {
            return traitsName;
        }

        public boolean getRequiresElementBitSize()
        {
            return requiresElementBitSize;
        }

        public boolean getRequiresElementCreator()
        {
            return requiresElementCreator;
        }

        public boolean getIsImplicit()
        {
            return isImplicit;
        }

        public String getLength()
        {
            return length;
        }

        public String getElementPythonTypeName()
        {
            return elementPythonTypeName;
        }

        public BitSize getElementBitSize()
        {
            return elementBitSize;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        private static String createLength(ArrayType arrayType, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioEmitException
        {
            final Expression lengthExpression = arrayType.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return pythonExpressionFormatter.formatGetter(lengthExpression);
        }

        private final String traitsName;
        private final boolean requiresElementBitSize;
        private final boolean requiresElementCreator;
        private final boolean isImplicit;
        private final String length;
        private final String elementPythonTypeName;
        private final BitSize elementBitSize;
        private final Compound elementCompound;
    }

    public static class Compound
    {
        public Compound(CompoundType compoundFieldType)
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
        }

        public Compound(ExpressionFormatter pythonExpressionFormatter, TypeInstantiation compoundFieldType)
                throws ZserioEmitException
        {
            final List<InstantiatedParameter> parameters = compoundFieldType.getInstantiatedParameters();
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
                    InstantiatedParameter instantiatedParameter) throws ZserioEmitException
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

    private static RangeCheck createRangeCheck(ZserioType type, boolean withRangeCheckCode,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        // don't do range check for non-integer type
        if (!withRangeCheckCode || !(type instanceof IntegerType))
            return null;

        final IntegerType integerType = (IntegerType)type;
        final BitFieldWithExpression bitFieldWithExpression = createBitFieldWithExpression(type,
                pythonExpressionFormatter);

        final BigInteger zserioLowerBound = integerType.getLowerBound();
        final String lowerBound = zserioLowerBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioLowerBound) : null;

        final BigInteger zserioUpperBound = integerType.getUpperBound();
        final String upperBound = zserioUpperBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioUpperBound) : null;

        return new RangeCheck(bitFieldWithExpression, lowerBound, upperBound);
    }

    private static BitFieldWithExpression createBitFieldWithExpression(ZserioType type,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        if (type instanceof BitFieldType)
        {
            final BitFieldType bitFieldType = (BitFieldType)type;
            if (bitFieldType.getBitSize() == null)
                return new BitFieldWithExpression(bitFieldType, pythonExpressionFormatter);
        }

        return null;
    }

    private static Optional createOptional(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        if (!field.isOptional())
            return null;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, indicatorName, pythonExpressionFormatter);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createConstraint(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(constraintExpression);
    }

    private static Offset createOffset(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, pythonExpressionFormatter);
    }

    private static Array createArray(PythonNativeType nativeType, ZserioType baseType,
            PythonNativeTypeMapper pythonNativeTypeMapper, ExpressionFormatter pythonExpressionFormatter,
            ImportCollector importCollector) throws ZserioEmitException
    {
        if (!(baseType instanceof ArrayType))
            return null;

        if (!(nativeType instanceof NativeArrayType))
            throw new ZserioEmitException("Inconsistent base type '" + baseType.getClass() +
                    "' and native type '" + nativeType.getClass() + "'!");

        return new Array((NativeArrayType)nativeType, (ArrayType)baseType, pythonNativeTypeMapper,
                pythonExpressionFormatter, importCollector);
    }

    private static Compound createCompound(ExpressionFormatter pythonExpressionFormatter,
            TypeInstantiation fieldTypeInstantiation) throws ZserioEmitException
    {
        if (fieldTypeInstantiation.getTypeReference().getBaseTypeReference().getType() instanceof CompoundType)
            return new Compound(pythonExpressionFormatter, fieldTypeInstantiation);
        else
            return null;
    }

    private final String name;
    private final String pythonTypeName;
    private final String getterName;
    private final String setterName;

    private final RangeCheck rangeCheck;
    private final Optional optional;

    private final String alignmentValue;
    private final String initializer;
    private final String constraint;

    private final boolean usesChoiceMember;

    private final BitSize bitSize;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
}
