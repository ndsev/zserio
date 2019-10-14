package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeInstantiation.InstantiatedParameter;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeArrayType;
import zserio.emit.java.types.NativeBooleanType;
import zserio.emit.java.types.NativeDoubleType;
import zserio.emit.java.types.NativeEnumType;
import zserio.emit.java.types.NativeFloatType;
import zserio.emit.java.types.NativeIntegralType;
import zserio.emit.java.types.NativeLongType;
import zserio.emit.java.types.NativeObjectArrayType;

public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
            boolean withRangeCheckCode, CompoundType parentType, Field field,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final TypeReference fieldTypeReference = fieldTypeInstantiation.getTypeReference();
        final ZserioType fieldBaseType = fieldTypeReference.getBaseType();

        name = field.getName();

        // this must be the first one because we need to determine isTypeNullable
        optional = createOptional(field, javaExpressionFormatter);

        final boolean isTypeNullable = (optional != null);
        final JavaNativeType nativeType = (isTypeNullable)
                ? javaNativeTypeMapper.getNullableJavaType(fieldTypeReference)
                : javaNativeTypeMapper.getJavaType(fieldTypeReference);

        javaTypeName = nativeType.getFullName();
        javaNullableTypeName = javaNativeTypeMapper.getNullableJavaType(fieldTypeReference).getFullName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        rangeCheckData = new RangeCheckTemplateData(javaNativeTypeMapper, withRangeCheckCode, name,
                fieldBaseType, isTypeNullable, javaExpressionFormatter);

        alignmentValue = createAlignmentValue(field, javaExpressionFormatter);
        initializer = createInitializer(field, javaExpressionFormatter);

        usesObjectChoice = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isBool = nativeType instanceof NativeBooleanType;
        isLong = nativeType instanceof NativeLongType;
        isFloat = nativeType instanceof NativeFloatType;
        isDouble = nativeType instanceof NativeDoubleType;
        isEnum = nativeType instanceof NativeEnumType;
        isSimpleType = nativeType.isSimple();
        isObjectArray = nativeType instanceof NativeObjectArrayType;

        constraint = createConstraint(field, javaExpressionFormatter);

        bitSize = new BitSize(fieldBaseType, javaNativeTypeMapper, javaExpressionFormatter);
        offset = createOffset(field, javaNativeTypeMapper, javaExpressionFormatter);
        array = createArray(nativeType, fieldBaseType, parentType, javaNativeTypeMapper, withWriterCode,
                javaExpressionFormatter);
        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(fieldBaseType, javaExpressionFormatter,
                javaNativeTypeMapper);
        compound = createCompound(javaNativeTypeMapper, withWriterCode, javaExpressionFormatter, parentType,
                fieldTypeInstantiation);
    }

    public String getName()
    {
        return name;
    }

    public String getJavaTypeName()
    {
        return javaTypeName;
    }

    public String getJavaNullableTypeName()
    {
        return javaNullableTypeName;
    }

    public String getGetterName()
    {
        return getterName;
    }

    public String getSetterName()
    {
        return setterName;
    }

    public RangeCheckTemplateData getRangeCheckData()
    {
        return rangeCheckData;
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

    public boolean getUsesObjectChoice()
    {
        return usesObjectChoice;
    }

    public boolean getIsBool()
    {
        return isBool;
    }

    public boolean getIsLong()
    {
        return isLong;
    }

    public boolean getIsFloat()
    {
        return isFloat;
    }

    public boolean getIsDouble()
    {
        return isDouble;
    }

    public boolean getIsEnum()
    {
        return isEnum;
    }

    public boolean getIsSimpleType()
    {
        return isSimpleType;
    }

    public boolean getIsObjectArray()
    {
        return isObjectArray;
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
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            clause = (optionalClauseExpression == null) ? null :
                javaExpressionFormatter.formatGetter(optionalClauseExpression);
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

        private final String  clause;
        private final String  indicatorName;
    }

    public static class BitSize
    {
        public BitSize(ZserioType type, JavaNativeTypeMapper javaNativeTypeMapper,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            value = createValue(type, javaExpressionFormatter);
            runtimeFunction = (value != null) ? null :
                JavaRuntimeFunctionDataCreator.createData(type, javaExpressionFormatter, javaNativeTypeMapper);
        }

        public String getValue()
        {
            return value;
        }

        public RuntimeFunctionTemplateData getRuntimeFunction()
        {
            return runtimeFunction;
        }

        private static String createValue(ZserioType type, ExpressionFormatter javaExpressionFormatter)
                throws ZserioEmitException
        {
            String bitSizeOfValue = null;
            if (type instanceof FixedSizeType)
            {
                bitSizeOfValue = JavaLiteralFormatter.formatDecimalLiteral(((FixedSizeType)type).getBitSize());
            }
            else if (type instanceof BitFieldType)
            {
                final BitFieldType bitFieldType = (BitFieldType)type;
                final Integer bitSize = bitFieldType.getBitSize();
                bitSizeOfValue = (bitSize != null) ? JavaLiteralFormatter.formatDecimalLiteral(bitSize) :
                    javaExpressionFormatter.formatGetter(bitFieldType.getLengthExpression());
            }

            return bitSizeOfValue;
        }

        private final String                        value;
        private final RuntimeFunctionTemplateData   runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, JavaNativeTypeMapper javaNativeTypeMapper,
                     ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            getter = javaExpressionFormatter.formatGetter(offsetExpression);
            setter = javaExpressionFormatter.formatSetter(offsetExpression);
            final JavaNativeType nativeType =
                    javaNativeTypeMapper.getJavaType(offsetExpression.getExprZserioType());
            requiresBigInt = (nativeType instanceof NativeIntegralType) ?
                    ((NativeIntegralType)nativeType).requiresBigInt() : false;
            typeName = nativeType.getFullName();
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

        public String getTypeName()
        {
            return typeName;
        }

        public boolean getRequiresBigInt()
        {
            return requiresBigInt;
        }

        public boolean getContainsIndex()
        {
            return containsIndex;
        }

        private final String    getter;
        private final String    setter;
        private final String    typeName;
        private final boolean   requiresBigInt;
        private final boolean   containsIndex;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayType arrayType, CompoundType parentType,
                JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            final TypeInstantiation elementTypeInstantiation = arrayType.getElementTypeInstantiation();
            final TypeReference elementTypeReference = elementTypeInstantiation.getTypeReference();
            final ZserioType elementBaseType = elementTypeReference.getBaseType();

            isImplicit = arrayType.isImplicit();
            length = createLength(arrayType, javaExpressionFormatter);
            final JavaNativeType elementNativeType = javaNativeTypeMapper.getJavaType(elementBaseType);
            elementJavaTypeName = elementNativeType.getFullName();

            requiresElementBitSize = nativeType.requiresElementBitSize();
            requiresElementFactory = nativeType.requiresElementFactory();
            requiresParentContext = createRequiresParentContext(elementTypeInstantiation);

            generateListSetter = createGenerateListSetter(elementTypeInstantiation);

            elementBitSize = new BitSize(elementBaseType, javaNativeTypeMapper, javaExpressionFormatter);
            isElementEnum = elementNativeType instanceof NativeEnumType;
            elementCompound = createCompound(javaNativeTypeMapper, withWriterCode, javaExpressionFormatter,
                    parentType, elementTypeInstantiation);
        }

        public boolean getIsImplicit()
        {
            return isImplicit;
        }

        public String getLength()
        {
            return length;
        }

        public String getElementJavaTypeName()
        {
            return elementJavaTypeName;
        }

        public boolean getRequiresElementBitSize()
        {
            return requiresElementBitSize;
        }

        public boolean getRequiresElementFactory()
        {
            return requiresElementFactory;
        }

        public boolean getRequiresParentContext()
        {
            return requiresParentContext;
        }

        public boolean getGenerateListSetter()
        {
            return generateListSetter;
        }

        public BitSize getElementBitSize()
        {
            return elementBitSize;
        }

        public boolean getIsElementEnum()
        {
            return isElementEnum;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        private static String createLength(ArrayType arrayType, ExpressionFormatter javaExpressionFormatter)
                throws ZserioEmitException
        {
            final Expression lengthExpression = arrayType.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return javaExpressionFormatter.formatGetter(lengthExpression);
        }

        private static boolean createRequiresParentContext(TypeInstantiation elementTypeInstantiation)
        {
            /*
             * Array length expression (Foo field[expr];) is not needed here because it's handled by the array
             * class itself, it's not propagated to the array factory.
             * But an array can be composed of type instantiations and these need to be handled.
             */
            for (InstantiatedParameter instantiatedParameter :
                    elementTypeInstantiation.getInstantiatedParameters())
            {
                if (instantiatedParameter.getArgumentExpression().requiresOwnerContext())
                    return true;
            }

            return false;
        }

        private static boolean createGenerateListSetter(TypeInstantiation elementTypeInstantiation)
        {
            // TODO[Mi-L@][typeref] Check!
            final ZserioType elementBaseType = elementTypeInstantiation.getTypeReference().getBaseType();
            return elementBaseType instanceof CompoundType || elementBaseType instanceof EnumType ||
                    !elementTypeInstantiation.getInstantiatedParameters().isEmpty();
        }

        private final boolean       isImplicit;
        private final String        length;
        private final String        elementJavaTypeName;
        private final boolean       requiresElementBitSize;
        private final boolean       requiresElementFactory;
        private final boolean       requiresParentContext;
        private final boolean       generateListSetter;
        private final BitSize       elementBitSize;
        private final boolean       isElementEnum;
        private final Compound      elementCompound;
    }

    public static class Compound
    {
        public Compound(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
                CompoundType owner, CompoundType compoundFieldType)
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
        }

        public Compound(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
                ExpressionFormatter javaExpressionFormatter, CompoundType owner,
                TypeInstantiation compoundFieldType) throws ZserioEmitException
        {
            final List<InstantiatedParameter> parameters = compoundFieldType.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
                instantiatedParameters.add(new InstantiatedParameterData(javaNativeTypeMapper,
                        javaExpressionFormatter, parameter));
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public static class InstantiatedParameterData
        {
            public InstantiatedParameterData(JavaNativeTypeMapper javaNativeTypeMapper,
                    ExpressionFormatter javaExpressionFormatter, InstantiatedParameter instantiatedParameter)
                            throws ZserioEmitException
            {
                final TypeReference parameterTypeReference =
                        instantiatedParameter.getParameter().getTypeReference();
                final JavaNativeType nativeParameterType =
                        javaNativeTypeMapper.getJavaType(parameterTypeReference);
                javaTypeName = nativeParameterType.getFullName();
                isSimpleType = nativeParameterType.isSimple();
                expression = javaExpressionFormatter.formatGetter(
                        instantiatedParameter.getArgumentExpression());
            }

            public String getJavaTypeName()
            {
                return javaTypeName;
            }

            public boolean getIsSimpleType()
            {
                return isSimpleType;
            }

            public String getExpression()
            {
                return expression;
            }

            private final String    javaTypeName;
            private final boolean   isSimpleType;
            private final String    expression;
        }

        final ArrayList<InstantiatedParameterData>  instantiatedParameters;
    }

    private static Optional createOptional(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioEmitException
    {
        if (!field.isOptional())
            return null;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, indicatorName, javaExpressionFormatter);
    }

    private static String createInitializer(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createConstraint(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(constraintExpression);
    }

    private static Offset createOffset(Field field, JavaNativeTypeMapper javaNativeTypeMapper,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, javaNativeTypeMapper, javaExpressionFormatter);
    }

    private static Array createArray(JavaNativeType nativeType, ZserioType baseType, CompoundType parentType,
            JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        if (!(nativeType instanceof NativeArrayType))
            return null;

        if (!(baseType instanceof ArrayType))
            throw new ZserioEmitException("Inconsistent base type '" + baseType.getClass() +
                    "' and native type '" + nativeType.getClass() + "'!");

        return new Array((NativeArrayType)nativeType, (ArrayType)baseType, parentType, javaNativeTypeMapper,
                withWriterCode, javaExpressionFormatter);
    }

    private static Compound createCompound(JavaNativeTypeMapper javaNativeTypeMapper, boolean withWriterCode,
            ExpressionFormatter javaExpressionFormatter, CompoundType owner,
            TypeInstantiation fieldTypeInstantiation) throws ZserioEmitException
    {
        if (fieldTypeInstantiation.getTypeReference().getBaseType() instanceof CompoundType)
            return new Compound(javaNativeTypeMapper, withWriterCode, javaExpressionFormatter, owner,
                    fieldTypeInstantiation);
        else
            return null;
    }

    private final String                        name;
    private final String                        javaTypeName;
    private final String                        javaNullableTypeName;
    private final String                        getterName;
    private final String                        setterName;
    private final RangeCheckTemplateData        rangeCheckData;
    private final Optional                      optional;
    private final String                        alignmentValue;
    private final String                        initializer;

    private final boolean                       usesObjectChoice;

    private final boolean                       isBool;
    private final boolean                       isLong;
    private final boolean                       isFloat;
    private final boolean                       isDouble;
    private final boolean                       isEnum;
    private final boolean                       isSimpleType;
    private final boolean                       isObjectArray;
    private final String                        constraint;

    private final BitSize                       bitSize;
    private final Offset                        offset;
    private final Array                         array;
    private final RuntimeFunctionTemplateData   runtimeFunction;
    private final Compound                      compound;
}
