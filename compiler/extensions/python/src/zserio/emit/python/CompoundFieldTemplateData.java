package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.CompoundType;
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
            Field fieldType, ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        name = fieldType.getName();

        final ZserioType baseType = TypeReference.resolveBaseType(fieldType.getFieldType());
        final PythonNativeType nativeType = pythonNativeTypeMapper.getPythonType(baseType);
        pythonTypeName = nativeType.getFullName();

        getterName = AccessorNameFormatter.getGetterName(fieldType);
        setterName = AccessorNameFormatter.getSetterName(fieldType);

        /* TODO
        rangeCheckData = new RangeCheckTemplateData(); */
        optional = createOptional(fieldType, pythonExpressionFormatter);

        alignmentValue = createAlignmentValue(fieldType, pythonExpressionFormatter);
        initializer = createInitializer(fieldType, pythonExpressionFormatter);
        constraint = createConstraint(fieldType, pythonExpressionFormatter);

        bitSize = new BitSize(baseType, pythonExpressionFormatter);
        offset = createOffset(fieldType, pythonExpressionFormatter);
        array = createArray(nativeType, baseType, pythonNativeTypeMapper, pythonExpressionFormatter);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(baseType, pythonExpressionFormatter);
        compound = createCompound(pythonExpressionFormatter, baseType);
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

    /* TODO
    public RangeCheckTemplateData getRangeCheckData()
    {
        return rangeCheckData;
    }*/

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
        public Array(NativeArrayType nativeType, ArrayType baseType,
                PythonNativeTypeMapper pythonNativeTypeMapper, ExpressionFormatter pythonExpressionFormatter)
                        throws ZserioEmitException
        {
            traitsName = nativeType.getTraitsName();
            requiresElementBitSize = nativeType.getRequiresElementBitSize();
            requiresElementCreator = nativeType.getRequiresElementCreator();

            isImplicit = baseType.isImplicit();
            length = createLength(baseType, pythonExpressionFormatter);

            final ZserioType elementType = TypeReference.resolveBaseType(baseType.getElementType());
            final PythonNativeType elementNativeType = pythonNativeTypeMapper.getPythonType(elementType);
            elementPythonTypeName = elementNativeType.getFullName();
            elementBitSize = new BitSize(elementType, pythonExpressionFormatter);
            elementCompound = createCompound(pythonExpressionFormatter, elementType);
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
                expression = pythonExpressionFormatter.formatGetter(
                        instantiatedParameter.getArgumentExpression());
            }

            public String getExpression()
            {
                return expression;
            }

            private final String expression;
        }

        final List<InstantiatedParameterData> instantiatedParameters;
    }

    private static Optional createOptional(Field fieldType, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        if (!fieldType.getIsOptional())
            return null;

        final Expression optionalClauseExpression = fieldType.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(fieldType);

        return new Optional(optionalClauseExpression, indicatorName, pythonExpressionFormatter);
    }

    private static String createAlignmentValue(Field fieldType, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression alignmentExpression = fieldType.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field fieldType, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression initializerExpression = fieldType.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createConstraint(Field fieldType, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression constraintExpression = fieldType.getConstraintExpr();
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
            PythonNativeTypeMapper pythonNativeTypeMapper, ExpressionFormatter pythonExpressionFormatter)
                    throws ZserioEmitException
    {
        if (!(baseType instanceof ArrayType))
            return null;

        if (!(nativeType instanceof NativeArrayType))
            throw new ZserioEmitException("Inconsistent base type '" + baseType.getClass() +
                    "' and native type '" + nativeType.getClass() + "'!");

        return new Array((NativeArrayType)nativeType, (ArrayType)baseType, pythonNativeTypeMapper,
                pythonExpressionFormatter);
    }

    private static Compound createCompound(ExpressionFormatter pythonExpressionFormatter, ZserioType baseType)
                    throws ZserioEmitException
    {
        if (baseType instanceof CompoundType)
            return new Compound((CompoundType)baseType);
        else if (baseType instanceof TypeInstantiation)
            return new Compound(pythonExpressionFormatter, (TypeInstantiation)baseType);
        else
            return null;
    }

    private final String name;
    private final String pythonTypeName;
    private final String getterName;
    private final String setterName;

    //TODO private final RangeCheckTemplateData rangeCheckData;
    private final Optional optional;

    private final String alignmentValue;
    private final String initializer;
    private final String constraint;

    private final BitSize bitSize;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
}
