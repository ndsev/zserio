package zserio.extension.java;

import java.util.ArrayList;

import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ArrayInstantiation;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeArrayTraits;
import zserio.extension.java.types.NativeArrayType;
import zserio.extension.java.types.NativeRawArray;

/**
 * FreeMarker template data for compound fields.
 */
public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(TemplateDataContext context, CompoundType parentType, Field field)
            throws ZserioExtensionException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();

        name = field.getName();

        // this must be the first one because we need to determine isTypeNullable
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final ExpressionFormatter javaLambdaExpressionFormatter = context.getJavaLambdaExpressionFormatter();
        optional = createOptional(field, fieldTypeInstantiation.getBaseType(), parentType,
                javaExpressionFormatter, javaLambdaExpressionFormatter);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final boolean isTypeNullable = (optional != null);
        final JavaNativeType nullableNativeType = javaNativeMapper.getNullableJavaType(fieldTypeInstantiation);
        final JavaNativeType nativeType = (isTypeNullable) ? nullableNativeType :
                javaNativeMapper.getJavaType(fieldTypeInstantiation);

        nullableTypeInfo = new NativeTypeInfoTemplateData(nullableNativeType, fieldTypeInstantiation);
        typeInfo = new NativeTypeInfoTemplateData(nativeType, fieldTypeInstantiation);

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        isExtended = field.isExtended();
        isPresentIndicatorName = AccessorNameFormatter.getIsPresentIndicatorName(field);
        isPackable = field.isPackable();

        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        rangeCheckData = new RangeCheckTemplateData(this, javaNativeMapper, withRangeCheckCode,
                fieldTypeInstantiation, isTypeNullable, javaExpressionFormatter);

        alignmentValue = createAlignmentValue(field, javaExpressionFormatter);
        initializer = createInitializer(field, javaExpressionFormatter);

        usesObjectChoice = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        constraint = createConstraint(field, javaExpressionFormatter);
        lambdaConstraint = createConstraint(field, javaLambdaExpressionFormatter);

        bitSize = BitSizeTemplateData.create(fieldTypeInstantiation, javaExpressionFormatter,
                javaLambdaExpressionFormatter);
        offset = createOffset(field, javaNativeMapper, javaExpressionFormatter, javaLambdaExpressionFormatter);
        array = createArray(context, nativeType, fieldTypeInstantiation, parentType);
        runtimeFunction = RuntimeFunctionDataCreator.createData(context, fieldTypeInstantiation);
        compound = createCompound(context, fieldTypeInstantiation);
        docComments = DocCommentsDataCreator.createData(context, field);
    }

    public String getName()
    {
        return name;
    }

    public NativeTypeInfoTemplateData getNullableTypeInfo()
    {
        return nullableTypeInfo;
    }

    public NativeTypeInfoTemplateData getTypeInfo()
    {
        return typeInfo;
    }

    public String getGetterName()
    {
        return getterName;
    }

    public String getSetterName()
    {
        return setterName;
    }

    public boolean getIsExtended()
    {
        return isExtended;
    }

    public String getIsPresentIndicatorName()
    {
        return isPresentIndicatorName;
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

    public String getConstraint()
    {
        return constraint;
    }

    public String getLambdaConstraint()
    {
        return lambdaConstraint;
    }

    public BitSizeTemplateData getBitSize()
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

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public static class Optional
    {
        public Optional(Field field, ExpressionFormatter javaExpressionFormatter,
                ExpressionFormatter javaLambdaExpressionFormatter, boolean isRecursive)
                        throws ZserioExtensionException
        {
            final Expression optionalClauseExpression = field.getOptionalClauseExpr();
            clause = (optionalClauseExpression == null) ? null :
                javaExpressionFormatter.formatGetter(optionalClauseExpression);
            lambdaClause = (optionalClauseExpression == null) ? null :
                javaLambdaExpressionFormatter.formatGetter(optionalClauseExpression);
            isUsedIndicatorName = AccessorNameFormatter.getIsUsedIndicatorName(field);
            isSetIndicatorName = AccessorNameFormatter.getIsSetIndicatorName(field);
            resetterName = AccessorNameFormatter.getResetterName(field);
            this.isRecursive = isRecursive;
        }

        public String getClause()
        {
            return clause;
        }

        public String getLambdaClause()
        {
            return lambdaClause;
        }

        public String getIsUsedIndicatorName()
        {
            return isUsedIndicatorName;
        }

        public String getIsSetIndicatorName()
        {
            return isSetIndicatorName;
        }

        public String getResetterName()
        {
            return resetterName;
        }

        public boolean getIsRecursive()
        {
            return isRecursive;
        }

        private final String clause;
        private final String lambdaClause;
        private final String isUsedIndicatorName;
        private final String isSetIndicatorName;
        private final String resetterName;
        private final boolean isRecursive;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, JavaNativeMapper javaNativeMapper,
                     ExpressionFormatter javaExpressionFormatter,
                     ExpressionFormatter javaLambdaExpressionFormatter) throws ZserioExtensionException
        {
            getter = javaExpressionFormatter.formatGetter(offsetExpression);
            setter = javaExpressionFormatter.formatSetter(offsetExpression);
            lambdaGetter = javaLambdaExpressionFormatter.formatGetter(offsetExpression);
            final JavaNativeType nativeType =
                    javaNativeMapper.getJavaType(offsetExpression.getExprZserioType());
            typeInfo = new NativeTypeInfoTemplateData(nativeType);
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

        public String getLambdaGetter()
        {
            return lambdaGetter;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public boolean getContainsIndex()
        {
            return containsIndex;
        }

        private final String getter;
        private final String setter;
        private final String lambdaGetter;
        private final NativeTypeInfoTemplateData typeInfo;
        private final boolean containsIndex;
    }

    public static class Array
    {
        public Array(TemplateDataContext context, NativeArrayType nativeType,
                ArrayInstantiation arrayInstantiation, CompoundType parentType) throws ZserioExtensionException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();

            isImplicit = arrayInstantiation.isImplicit();
            isPacked = arrayInstantiation.isPacked();

            final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
            length = createLength(arrayInstantiation, javaExpressionFormatter);
            final ExpressionFormatter javaLambdaExpressionFormatter =
                    context.getJavaLambdaExpressionFormatter();
            lambdaLength = createLength(arrayInstantiation, javaLambdaExpressionFormatter);

            wrapperJavaTypeName = nativeType.getArrayWrapper().getFullName();
            final NativeRawArray nativeRawArray = nativeType.getRawArray();
            rawHolderJavaTypeName = nativeRawArray.getFullName();

            final NativeArrayTraits nativeArrayTraits = nativeType.getArrayTraits();
            arrayTraits = new ArrayTraitsTemplateData(nativeArrayTraits);

            final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
            final JavaNativeType elementNativeType = javaNativeMapper.getJavaType(elementTypeInstantiation);
            elementTypeInfo = new NativeTypeInfoTemplateData(elementNativeType, elementTypeInstantiation);

            requiresElementClass = nativeRawArray.requiresElementClass();
            requiresParentContext = createRequiresParentContext(elementTypeInstantiation);

            elementBitSize = BitSizeTemplateData.create(elementTypeInstantiation, javaExpressionFormatter,
                    javaLambdaExpressionFormatter);
            elementCompound = createCompound(context, elementTypeInstantiation);
            elementIsRecursive = elementTypeInstantiation.getBaseType() == parentType;
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

        public String getLambdaLength()
        {
            return lambdaLength;
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

        public NativeTypeInfoTemplateData getElementTypeInfo()
        {
            return elementTypeInfo;
        }

        public boolean getRequiresElementClass()
        {
            return requiresElementClass;
        }

        public boolean getRequiresParentContext()
        {
            return requiresParentContext;
        }

        public BitSizeTemplateData getElementBitSize()
        {
            return elementBitSize;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        public boolean getElementIsRecursive()
        {
            return elementIsRecursive;
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
        private final String lambdaLength;
        private final String wrapperJavaTypeName;
        private final String rawHolderJavaTypeName;
        private final ArrayTraitsTemplateData arrayTraits;
        private final NativeTypeInfoTemplateData elementTypeInfo;
        private final boolean requiresElementClass;
        private final boolean requiresParentContext;
        private final BitSizeTemplateData elementBitSize;
        private final Compound elementCompound;
        private final boolean elementIsRecursive;
    }

    public static class Compound
    {
        public Compound(TemplateDataContext context,
                ParameterizedTypeInstantiation parameterizedTypeInstantiation) throws ZserioExtensionException
        {
            this(context, parameterizedTypeInstantiation.getBaseType());

            for (InstantiatedParameter param : parameterizedTypeInstantiation.getInstantiatedParameters())
            {
                instantiatedParameters.add(new InstantiatedParameterData(context, param));
            }
        }

        public Compound(TemplateDataContext context, CompoundType compoundType) throws ZserioExtensionException
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>();
            parameters = new CompoundParameterTemplateData(context, compoundType);
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public CompoundParameterTemplateData getParameters()
        {
            return parameters;
        }

        public static class InstantiatedParameterData
        {
            public InstantiatedParameterData(TemplateDataContext context,
                    InstantiatedParameter instantiatedParameter) throws ZserioExtensionException
            {
                final TypeReference parameterTypeReference =
                        instantiatedParameter.getParameter().getTypeReference();
                final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
                final JavaNativeType nativeParameterType =
                        javaNativeMapper.getJavaType(parameterTypeReference);
                typeInfo = new NativeTypeInfoTemplateData(nativeParameterType, parameterTypeReference);
                final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
                expression = javaExpressionFormatter.formatGetter(
                        instantiatedParameter.getArgumentExpression());
                final ExpressionFormatter javaLambdaExpressionFormatter =
                        context.getJavaLambdaExpressionFormatter();
                lambdaExpression = javaLambdaExpressionFormatter.formatGetter(
                        instantiatedParameter.getArgumentExpression());
            }

            public NativeTypeInfoTemplateData getTypeInfo()
            {
                return typeInfo;
            }

            public String getExpression()
            {
                return expression;
            }

            public String getLambdaExpression()
            {
                return lambdaExpression;
            }

            private final NativeTypeInfoTemplateData typeInfo;
            private final String expression;
            private final String lambdaExpression;
        }

        private final ArrayList<InstantiatedParameterData> instantiatedParameters;
        private final CompoundParameterTemplateData parameters;
    }

    private static Optional createOptional(Field field, ZserioType fieldBaseType, CompoundType parentType,
            ExpressionFormatter javaExpressionFormatter, ExpressionFormatter javaLambdaExpressionFormatter)
                    throws ZserioExtensionException
    {
        if (!field.isOptional())
            return null;

        final boolean isRecursive = fieldBaseType == parentType;

        return new Optional(field, javaExpressionFormatter, javaLambdaExpressionFormatter, isRecursive);
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
            ExpressionFormatter javaExpressionFormatter, ExpressionFormatter javaLambdaExpressionFormatter)
                    throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, javaNativeMapper, javaExpressionFormatter,
                javaLambdaExpressionFormatter);
    }

    private static Array createArray(TemplateDataContext context, JavaNativeType nativeType,
            TypeInstantiation typeInstantiation, CompoundType parentType) throws ZserioExtensionException
    {
        if (!(nativeType instanceof NativeArrayType))
            return null;

        if (!(typeInstantiation instanceof ArrayInstantiation))
        {
            throw new ZserioExtensionException("Inconsistent instantiation '" +
                    typeInstantiation.getClass().getName() + "' and native type '" +
                    nativeType.getClass().getName() + "'!");
        }

        return new Array(context, (NativeArrayType)nativeType,
                (ArrayInstantiation)typeInstantiation, parentType);
    }

    private static Compound createCompound(TemplateDataContext context, TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(context, (ParameterizedTypeInstantiation)typeInstantiation);
        }
        else if (typeInstantiation.getBaseType() instanceof CompoundType)
        {
            return new Compound(context, (CompoundType)typeInstantiation.getBaseType());
        }
        else
        {
            return null;
        }
    }

    private final String name;
    private final NativeTypeInfoTemplateData nullableTypeInfo;
    private final NativeTypeInfoTemplateData typeInfo;
    private final String getterName;
    private final String setterName;
    private final boolean isExtended;
    private final String isPresentIndicatorName;
    private final boolean isPackable;
    private final RangeCheckTemplateData rangeCheckData;
    private final Optional optional;
    private final String alignmentValue;
    private final String initializer;
    private final boolean usesObjectChoice;
    private final String constraint;
    private final String lambdaConstraint;
    private final BitSizeTemplateData bitSize;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
    private final DocCommentsTemplateData docComments;
}
