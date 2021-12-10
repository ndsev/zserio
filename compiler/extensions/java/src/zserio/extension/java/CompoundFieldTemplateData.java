package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ArrayInstantiation;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeArrayTraits;
import zserio.extension.java.types.NativeArrayType;
import zserio.extension.java.types.NativeArrayableType;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeDoubleType;
import zserio.extension.java.types.NativeEnumType;
import zserio.extension.java.types.NativeFloatType;
import zserio.extension.java.types.NativeIntegralType;
import zserio.extension.java.types.NativeLongType;
import zserio.extension.java.types.NativeRawArray;

public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(JavaNativeMapper javaNativeMapper,
            boolean withRangeCheckCode, CompoundType parentType, Field field,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();

        name = field.getName();

        // this must be the first one because we need to determine isTypeNullable
        optional = createOptional(field, fieldTypeInstantiation.getBaseType(), parentType,
                javaExpressionFormatter);

        final boolean isTypeNullable = (optional != null);
        final JavaNativeType nullableNativeType = javaNativeMapper.getNullableJavaType(fieldTypeInstantiation);
        final JavaNativeType nativeType = (isTypeNullable) ? nullableNativeType :
                javaNativeMapper.getJavaType(fieldTypeInstantiation);

        javaTypeName = nativeType.getFullName();
        javaNullableTypeName = nullableNativeType.getFullName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        isPackable = field.isPackable();

        rangeCheckData = new RangeCheckTemplateData(this, javaNativeMapper, withRangeCheckCode,
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
        isIntegralType = !(fieldTypeInstantiation instanceof ArrayInstantiation) &&
                (nativeType instanceof NativeIntegralType);

        constraint = createConstraint(field, javaExpressionFormatter);

        bitSize = new BitSize(fieldTypeInstantiation, javaNativeMapper, javaExpressionFormatter);
        offset = createOffset(field, javaNativeMapper, javaExpressionFormatter);
        arrayableInfo = createArrayableInfo(nativeType);
        array = createArray(nativeType, fieldTypeInstantiation, parentType, javaNativeMapper,
                javaExpressionFormatter);
        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(fieldTypeInstantiation,
                javaExpressionFormatter, javaNativeMapper);
        compound = createCompound(javaNativeMapper, javaExpressionFormatter, parentType,
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

    public boolean getIsPackable()
    {
        return isPackable;
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

    public boolean getIsIntegralType()
    {
        return isIntegralType;
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

    public ArrayableInfoTemplateData getArrayableInfo()
    {
        return arrayableInfo;
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
        public Optional(Expression optionalClauseExpression, String indicatorName, boolean isRecursive,
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
        {
            clause = (optionalClauseExpression == null) ? null :
                javaExpressionFormatter.formatGetter(optionalClauseExpression);
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
        public BitSize(TypeInstantiation typeInstantiation, JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
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
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
        {
            String value = null;
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                value = JavaLiteralFormatter.formatIntLiteral(
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

        private final String value;
        private final RuntimeFunctionTemplateData runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, JavaNativeMapper javaNativeMapper,
                     ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
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

        private final String getter;
        private final String setter;
        private final String typeName;
        private final boolean requiresBigInt;
        private final boolean containsIndex;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayInstantiation arrayInstantiation,
                CompoundType parentType, JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();

            isImplicit = arrayInstantiation.isImplicit();
            isPacked = arrayInstantiation.isPacked();

            length = createLength(arrayInstantiation, javaExpressionFormatter);

            wrapperJavaTypeName = nativeType.getArrayWrapper().getFullName();
            final NativeRawArray nativeRawArray = nativeType.getRawArray();
            rawHolderJavaTypeName = nativeRawArray.getFullName();

            final NativeArrayTraits nativeArrayTraits = nativeType.getArrayTraits();
            arrayTraits = new ArrayTraitsTemplateData(nativeArrayTraits);

            final JavaNativeType elementNativeType = javaNativeMapper.getJavaType(elementTypeInstantiation);
            elementJavaTypeName = elementNativeType.getFullName();

            requiresElementClass = nativeRawArray.requiresElementClass();
            requiresParentContext = createRequiresParentContext(elementTypeInstantiation);

            elementBitSize = new BitSize(elementTypeInstantiation, javaNativeMapper, javaExpressionFormatter);
            isElementEnum = elementNativeType instanceof NativeEnumType;
            elementCompound = createCompound(javaNativeMapper, javaExpressionFormatter,
                    parentType, elementTypeInstantiation);
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

        public String getWrapperJavaTypeName()
        {
            return wrapperJavaTypeName;
        }

        public String getRawHolderJavaTypeName()
        {
            return rawHolderJavaTypeName;
        }

        public ArrayTraitsTemplateData getArrayTraits()
        {
            return arrayTraits;
        }

        public String getElementJavaTypeName()
        {
            return elementJavaTypeName;
        }

        public boolean getRequiresElementClass()
        {
            return requiresElementClass;
        }

        public boolean getRequiresParentContext()
        {
            return requiresParentContext;
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
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
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

        private final boolean isImplicit;
        private final boolean isPacked;
        private final String length;
        private final String wrapperJavaTypeName;
        private final String rawHolderJavaTypeName;
        private final ArrayTraitsTemplateData arrayTraits;
        private final String elementJavaTypeName;
        private final boolean requiresElementClass;
        private final boolean requiresParentContext;
        private final BitSize elementBitSize;
        private final boolean isElementEnum;
        private final Compound elementCompound;
    }

    public static class Compound
    {
        public Compound()
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
        }

        public Compound(JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter, CompoundType owner,
                ParameterizedTypeInstantiation typeInstantiation) throws ZserioExtensionException
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
                            throws ZserioExtensionException
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

            private final String javaTypeName;
            private final boolean isSimpleType;
            private final String expression;
        }

        private final ArrayList<InstantiatedParameterData> instantiatedParameters;
    }

    private static Optional createOptional(Field field, ZserioType fieldBaseType, CompoundType parentType,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        if (!field.isOptional())
            return null;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);
        final boolean isRecursive = fieldBaseType == parentType;

        return new Optional(optionalClauseExpression, indicatorName, isRecursive, javaExpressionFormatter);
    }

    private static String createInitializer(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createConstraint(Field field, ExpressionFormatter javaExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return javaExpressionFormatter.formatGetter(constraintExpression);
    }

    private static Offset createOffset(Field field, JavaNativeMapper javaNativeMapper,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, javaNativeMapper, javaExpressionFormatter);
    }

    private static ArrayableInfoTemplateData createArrayableInfo(JavaNativeType nativeType)
    {
        if (nativeType instanceof NativeArrayableType)
            return new ArrayableInfoTemplateData((NativeArrayableType)nativeType);
        else
            return null;
    }

    private static Array createArray(JavaNativeType nativeType, TypeInstantiation typeInstantiation,
            CompoundType parentType, JavaNativeMapper javaNativeMapper,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        if (!(nativeType instanceof NativeArrayType))
            return null;

        if (!(typeInstantiation instanceof ArrayInstantiation))
        {
            throw new ZserioExtensionException("Inconsistent instantiation '" +
                    typeInstantiation.getClass().getName() + "' and native type '" +
                    nativeType.getClass().getName() + "'!");
        }

        return new Array((NativeArrayType)nativeType, (ArrayInstantiation)typeInstantiation, parentType,
                javaNativeMapper, javaExpressionFormatter);
    }

    private static Compound createCompound(JavaNativeMapper javaNativeMapper,
            ExpressionFormatter javaExpressionFormatter, CompoundType owner,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(javaNativeMapper, javaExpressionFormatter, owner,
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

    private final String name;
    private final String javaTypeName;
    private final String javaNullableTypeName;
    private final String getterName;
    private final String setterName;
    private final boolean isPackable;
    private final RangeCheckTemplateData rangeCheckData;
    private final Optional optional;
    private final String alignmentValue;
    private final String initializer;
    private final boolean usesObjectChoice;
    private final boolean isBool;
    private final boolean isLong;
    private final boolean isFloat;
    private final boolean isDouble;
    private final boolean isEnum;
    private final boolean isSimpleType;
    private final boolean isIntegralType;
    private final String constraint;
    private final BitSize bitSize;
    private final Offset offset;
    private final ArrayableInfoTemplateData arrayableInfo;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
}
