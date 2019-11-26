package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ArrayInstantiation;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
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
    public CompoundFieldTemplateData(JavaNativeMapper javaNativeMapper, boolean withWriterCode,
            boolean withRangeCheckCode, CompoundType parentType, Field field,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();

        name = field.getName();

        // this must be the first one because we need to determine isTypeNullable
        optional = createOptional(field, javaExpressionFormatter);

        final boolean isTypeNullable = (optional != null);
        final JavaNativeType nativeType = (isTypeNullable)
                ? javaNativeMapper.getNullableJavaType(fieldTypeInstantiation)
                : javaNativeMapper.getJavaType(fieldTypeInstantiation);

        javaTypeName = nativeType.getFullName();
        javaNullableTypeName = javaNativeMapper.getNullableJavaType(fieldTypeInstantiation).getFullName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        rangeCheckData = new RangeCheckTemplateData(javaNativeMapper, withRangeCheckCode, name,
                fieldTypeInstantiation, isTypeNullable, javaExpressionFormatter);

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

        bitSize = new BitSize(fieldTypeInstantiation, javaNativeMapper, javaExpressionFormatter);
        offset = createOffset(field, javaNativeMapper, javaExpressionFormatter);
        array = createArray(nativeType, fieldTypeInstantiation, parentType, javaNativeMapper, withWriterCode,
                javaExpressionFormatter);
        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(fieldTypeInstantiation,
                javaExpressionFormatter, javaNativeMapper);
        compound = createCompound(javaNativeMapper, withWriterCode, javaExpressionFormatter, parentType,
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
        public BitSize(TypeInstantiation typeInstantiation, JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            value = createValue(typeInstantiation, javaExpressionFormatter);
            runtimeFunction = (value != null) ? null :
                    JavaRuntimeFunctionDataCreator.createData(
                            typeInstantiation, javaExpressionFormatter, javaNativeMapper);
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
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            String value = null;
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                value = JavaLiteralFormatter.formatDecimalLiteral(
                        ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            }
            else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)typeInstantiation;
                value = javaExpressionFormatter.formatGetter(
                        dynamicBitFieldInstantiation.getLengthExpression());
            }

            return value;
        }

        private final String                        value;
        private final RuntimeFunctionTemplateData   runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, JavaNativeMapper javaNativeMapper,
                     ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            getter = javaExpressionFormatter.formatGetter(offsetExpression);
            setter = javaExpressionFormatter.formatSetter(offsetExpression);
            final JavaNativeType nativeType =
                    javaNativeMapper.getJavaType(offsetExpression.getExprZserioType());
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
        public Array(NativeArrayType nativeType, ArrayInstantiation arrayInstantiation, CompoundType parentType,
                JavaNativeMapper javaNativeMapper, boolean withWriterCode,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();

            isImplicit = arrayInstantiation.isImplicit();
            length = createLength(arrayInstantiation, javaExpressionFormatter);
            final JavaNativeType elementNativeType = javaNativeMapper.getJavaType(elementTypeInstantiation);
            elementJavaTypeName = elementNativeType.getFullName();

            requiresElementBitSize = nativeType.requiresElementBitSize();
            requiresElementFactory = nativeType.requiresElementFactory();
            requiresParentContext = createRequiresParentContext(elementTypeInstantiation);

            generateListSetter = createGenerateListSetter(elementTypeInstantiation);

            elementBitSize = new BitSize(elementTypeInstantiation, javaNativeMapper, javaExpressionFormatter);
            isElementEnum = elementNativeType instanceof NativeEnumType;
            elementCompound = createCompound(javaNativeMapper, withWriterCode, javaExpressionFormatter,
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

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
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
            if (elementTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)elementTypeInstantiation;
                for (InstantiatedParameter instantiatedParameter :
                        parameterizedInstantiation.getInstantiatedParameters())
                {
                    if (instantiatedParameter.getArgumentExpression().requiresOwnerContext())
                        return true;
                }
            }

            return false;
        }

        private static boolean createGenerateListSetter(TypeInstantiation elementTypeInstantiation)
        {
            final ZserioType elementBaseType = elementTypeInstantiation.getBaseType();

            boolean hasParameters = false;
            if (elementTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)elementTypeInstantiation;
                hasParameters = !parameterizedInstantiation.getInstantiatedParameters().isEmpty();
            }

            return elementBaseType instanceof CompoundType || elementBaseType instanceof EnumType ||
                    hasParameters;
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
        public Compound()
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
        }

        public Compound(JavaNativeMapper javaNativeMapper, boolean withWriterCode,
                ExpressionFormatter javaExpressionFormatter, CompoundType owner,
                ParameterizedTypeInstantiation typeInstantiation) throws ZserioEmitException
        {
            final List<InstantiatedParameter> parameters = typeInstantiation.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
                instantiatedParameters.add(new InstantiatedParameterData(javaNativeMapper,
                        javaExpressionFormatter, parameter));
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public static class InstantiatedParameterData
        {
            public InstantiatedParameterData(JavaNativeMapper javaNativeMapper,
                    ExpressionFormatter javaExpressionFormatter, InstantiatedParameter instantiatedParameter)
                            throws ZserioEmitException
            {
                final TypeReference parameterTypeReference =
                        instantiatedParameter.getParameter().getTypeReference();
                final JavaNativeType nativeParameterType =
                        javaNativeMapper.getJavaType(parameterTypeReference);
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

    private static Offset createOffset(Field field, JavaNativeMapper javaNativeMapper,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, javaNativeMapper, javaExpressionFormatter);
    }

    private static Array createArray(JavaNativeType nativeType, TypeInstantiation typeInstantiation,
            CompoundType parentType, JavaNativeMapper javaNativeMapper, boolean withWriterCode,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        if (!(nativeType instanceof NativeArrayType))
            return null;

        if (!(typeInstantiation instanceof ArrayInstantiation))
        {
            throw new ZserioEmitException("Inconsistent instantiation '" +
                    typeInstantiation.getClass().getName() + "' and native type '" +
                    nativeType.getClass().getName() + "'!");
        }

        return new Array((NativeArrayType)nativeType, (ArrayInstantiation)typeInstantiation, parentType,
                javaNativeMapper, withWriterCode, javaExpressionFormatter);
    }

    private static Compound createCompound(JavaNativeMapper javaNativeMapper, boolean withWriterCode,
            ExpressionFormatter javaExpressionFormatter, CompoundType owner,
            TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(javaNativeMapper, withWriterCode, javaExpressionFormatter, owner,
                    (ParameterizedTypeInstantiation)typeInstantiation);
        }
        else if (typeInstantiation.getBaseType() instanceof CompoundType)
        {
            return new Compound();
        }
        else
        {
            return null;
        }
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
