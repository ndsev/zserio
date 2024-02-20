package zserio.extension.cpp;

import java.math.BigInteger;
import java.util.ArrayList;

import zserio.ast.ArrayInstantiation;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.IntegerType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeArrayType;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * FreeMarker template data for compound fields.
 */
public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(TemplateDataContext context, CompoundType parentType, Field field,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final CppNativeType fieldNativeType = cppNativeMapper.getCppType(fieldTypeInstantiation);
        includeCollector.addHeaderIncludesForType(fieldNativeType);

        optional = (field.isOptional())
                ? createOptional(context, field, fieldBaseType, parentType, includeCollector)
                : null;
        compound = createCompound(context, fieldTypeInstantiation, includeCollector);

        name = field.getName();

        usesSharedPointer = usesSharedPointer(context, field, fieldBaseType);
        typeInfo = new NativeTypeInfoTemplateData(fieldNativeType, fieldTypeInstantiation);

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);
        readerName = AccessorNameFormatter.getReaderName(field);

        isExtended = field.isExtended();
        isPresentIndicatorName = AccessorNameFormatter.getIsPresentIndicatorName(field);
        isPackable = field.isPackable();

        integerRange = createIntegerRange(context, fieldTypeInstantiation, includeCollector);

        alignmentValue = createAlignmentValue(context, field, includeCollector);
        initializer = createInitializer(context, field, includeCollector);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        needsAllocator = !typeInfo.getIsSimple();
        holderNeedsAllocator = usesAnyHolder || (!usesSharedPointer && optional != null && optional.getIsRecursive());

        constraint = createConstraint(context, field, includeCollector);
        offset = createOffset(context, field, includeCollector);
        array = createArray(context, fieldNativeType, fieldTypeInstantiation, parentType, includeCollector);
        runtimeFunction =
                RuntimeFunctionDataCreator.createData(context, fieldTypeInstantiation, includeCollector);
        bitSize = BitSizeTemplateData.create(context, fieldTypeInstantiation, includeCollector);
        docComments = DocCommentsDataCreator.createData(context, field);
    }

    public Optional getOptional()
    {
        return optional;
    }

    public Compound getCompound()
    {
        return compound;
    }

    public String getName()
    {
        return name;
    }

    public boolean getUsesSharedPointer()
    {
        return usesSharedPointer;
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

    public String getReaderName()
    {
        return readerName;
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

    public IntegerRange getIntegerRange()
    {
        return integerRange;
    }

    public String getAlignmentValue()
    {
        return alignmentValue;
    }

    public String getInitializer()
    {
        return initializer;
    }

    public boolean getUsesAnyHolder()
    {
        return usesAnyHolder;
    }

    public boolean getNeedsAllocator()
    {
        return needsAllocator;
    }

    public boolean getHolderNeedsAllocator()
    {
        return holderNeedsAllocator;
    }

    public Constraint getConstraint()
    {
        return constraint;
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

    public BitSizeTemplateData getBitSize()
    {
        return bitSize;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public static final class Optional
    {
        public Optional(TemplateDataContext context, Field field, boolean isRecursive,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final Expression optionalClauseExpression = field.getOptionalClauseExpr();
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            clause = (optionalClauseExpression == null)
                    ? null
                    : cppExpressionFormatter.formatGetter(optionalClauseExpression);
            isUsedIndicatorName = AccessorNameFormatter.getIsUsedIndicatorName(field);
            isSetIndicatorName = AccessorNameFormatter.getIsSetIndicatorName(field);
            resetterName = AccessorNameFormatter.getResetterName(field);
            this.isRecursive = isRecursive;
        }

        public String getClause()
        {
            return clause;
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
        private final String isUsedIndicatorName;
        private final String isSetIndicatorName;
        private final String resetterName;
        private final boolean isRecursive;
    }

    public static final class Compound
    {
        public Compound(TemplateDataContext context,
                ParameterizedTypeInstantiation parameterizedTypeInstantiation,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            this(context, parameterizedTypeInstantiation.getBaseType(), includeCollector);

            for (InstantiatedParameter param : parameterizedTypeInstantiation.getInstantiatedParameters())
            {
                instantiatedParameters.add(new InstantiatedParameterData(context, param, includeCollector));
            }
        }

        public Compound(TemplateDataContext context, CompoundType compoundType,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>();
            parameters = new CompoundParameterTemplateData(context, compoundType, includeCollector);
            needsChildrenInitialization = compoundType.needsChildrenInitialization();
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public CompoundParameterTemplateData getParameters()
        {
            return parameters;
        }

        public boolean getNeedsChildrenInitialization()
        {
            return needsChildrenInitialization;
        }

        public static final class InstantiatedParameterData
        {
            public InstantiatedParameterData(TemplateDataContext context,
                    InstantiatedParameter instantiatedParameter, IncludeCollector includeCollector)
                    throws ZserioExtensionException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                final ExpressionFormatter cppExpressionFormatter =
                        context.getExpressionFormatter(includeCollector);
                expression = cppExpressionFormatter.formatGetter(argumentExpression);
                final ExpressionFormatter cppOwnerIndirectExpressionFormatter =
                        context.getIndirectExpressionFormatter(includeCollector, "owner");
                indirectExpression = cppOwnerIndirectExpressionFormatter.formatGetter(argumentExpression);
                needsOwner = argumentExpression.requiresOwnerContext();
                needsIndex = argumentExpression.containsIndex();
                final TypeReference parameterTypeReference =
                        instantiatedParameter.getParameter().getTypeReference();
                final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
                final CppNativeType cppNativeType = cppNativeMapper.getCppType(parameterTypeReference);
                typeInfo = new NativeTypeInfoTemplateData(cppNativeType, parameterTypeReference);
            }

            public String getExpression()
            {
                return expression;
            }

            public String getIndirectExpression()
            {
                return indirectExpression;
            }

            public boolean getNeedsOwner()
            {
                return needsOwner;
            }

            public boolean getNeedsIndex()
            {
                return needsIndex;
            }

            public NativeTypeInfoTemplateData getTypeInfo()
            {
                return typeInfo;
            }

            private final String expression;
            private final String indirectExpression;
            private final boolean needsOwner;
            private final boolean needsIndex;
            private final NativeTypeInfoTemplateData typeInfo;
        }

        private final ArrayList<InstantiatedParameterData> instantiatedParameters;
        private final CompoundParameterTemplateData parameters;
        private final boolean needsChildrenInitialization;
    }

    public static final class Constraint
    {
        public Constraint(Expression constraintExpression, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppConstraintExpressionFormatter) throws ZserioExtensionException
        {
            writeConstraint = cppExpressionFormatter.formatGetter(constraintExpression);
            readConstraint = cppConstraintExpressionFormatter.formatGetter(constraintExpression);
        }

        public String getWriteConstraint()
        {
            return writeConstraint;
        }

        public String getReadConstraint()
        {
            return readConstraint;
        }

        private final String writeConstraint;
        private final String readConstraint;
    }

    public static final class Offset
    {
        public Offset(TemplateDataContext context, Expression offsetExpression,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            getter = cppExpressionFormatter.formatGetter(offsetExpression);
            final ExpressionFormatter cppOwnerIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "owner");
            indirectGetter = cppOwnerIndirectExpressionFormatter.formatGetter(offsetExpression);
            setter = cppExpressionFormatter.formatSetter(offsetExpression);
            indirectSetter = cppOwnerIndirectExpressionFormatter.formatSetter(offsetExpression);
            final ZserioType offsetExprZserioType = offsetExpression.getExprZserioType();
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final CppNativeType nativeType = cppNativeMapper.getCppType(offsetExprZserioType);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, offsetExprZserioType);
            containsIndex = offsetExpression.containsIndex();
        }

        public String getGetter()
        {
            return getter;
        }

        public String getIndirectGetter()
        {
            return indirectGetter;
        }

        public String getSetter()
        {
            return setter;
        }

        public String getIndirectSetter()
        {
            return indirectSetter;
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
        private final String indirectGetter;
        private final String setter;
        private final String indirectSetter;
        private final NativeTypeInfoTemplateData typeInfo;
        private final boolean containsIndex;
    }

    public static final class IntegerRange
    {
        public IntegerRange(boolean checkLowerBound, String lowerBound, String upperBound,
                NativeIntegralTypeInfoTemplateData typeInfo, String bitFieldLength)
                throws ZserioExtensionException
        {
            this.checkLowerBound = checkLowerBound;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.typeInfo = typeInfo;
            this.bitFieldLength = bitFieldLength;
        }

        public boolean getCheckLowerBound()
        {
            return checkLowerBound;
        }

        public String getLowerBound()
        {
            return lowerBound;
        }

        public String getUpperBound()
        {
            return upperBound;
        }

        public NativeIntegralTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getBitFieldLength()
        {
            return bitFieldLength;
        }

        private final boolean checkLowerBound;
        private final String lowerBound;
        private final String upperBound;
        private final NativeIntegralTypeInfoTemplateData typeInfo;
        private final String bitFieldLength;
    }

    public static final class Array
    {
        public Array(TemplateDataContext context, NativeArrayType nativeType,
                ArrayInstantiation arrayInstantiation, CompoundType parentType,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();

            traits = new ArrayTraitsTemplateData(nativeType.getArrayTraits());
            isImplicit = arrayInstantiation.isImplicit();
            isPacked = arrayInstantiation.isPacked();
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            length = createLength(arrayInstantiation, cppExpressionFormatter);
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final CppNativeType elementNativeType = cppNativeMapper.getCppType(elementTypeInstantiation);
            includeCollector.addHeaderIncludesForType(elementNativeType);
            elementUsesSharedPointer = (elementTypeInstantiation.getBaseType() instanceof CompoundType) &&
                    isAboveTreshold(context, (CompoundType)elementTypeInstantiation.getBaseType());
            elementBitSize = BitSizeTemplateData.create(context, elementTypeInstantiation, includeCollector);
            elementCompound = createCompound(context, elementTypeInstantiation, includeCollector);
            elementIntegerRange = createIntegerRange(context, elementTypeInstantiation, includeCollector);
            elementIsRecursive = elementTypeInstantiation.getBaseType() == parentType;
            elementTypeInfo = new NativeTypeInfoTemplateData(elementNativeType, elementTypeInstantiation);
            elementUsedInPackedArray = context.getPackedTypesCollector().isUsedInPackedArray(
                    elementTypeInstantiation.getBaseType());
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

        public boolean getElementUsesSharedPointer()
        {
            return elementUsesSharedPointer;
        }

        public BitSizeTemplateData getElementBitSize()
        {
            return elementBitSize;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        public IntegerRange getElementIntegerRange()
        {
            return elementIntegerRange;
        }

        public boolean getElementIsRecursive()
        {
            return elementIsRecursive;
        }

        public NativeTypeInfoTemplateData getElementTypeInfo()
        {
            return elementTypeInfo;
        }

        public boolean getElementUsedInPackedArray()
        {
            return elementUsedInPackedArray;
        }

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return cppExpressionFormatter.formatGetter(lengthExpression);
        }

        private final ArrayTraitsTemplateData traits;
        private final boolean isImplicit;
        private final boolean isPacked;
        private final String length;
        private final boolean elementUsesSharedPointer;
        private final BitSizeTemplateData elementBitSize;
        private final Compound elementCompound;
        private final IntegerRange elementIntegerRange;
        private final boolean elementIsRecursive;
        private final NativeTypeInfoTemplateData elementTypeInfo;
        private final boolean elementUsedInPackedArray;
    }

    private static Optional createOptional(TemplateDataContext context, Field field, ZserioType baseFieldType,
            CompoundType parentType, IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final boolean isRecursive = baseFieldType == parentType;

        return new Optional(context, field, isRecursive, includeCollector);
    }

    private static IntegerRange createIntegerRange(
            TemplateDataContext context, TypeInstantiation typeInstantiation, IncludeCollector includeCollector)
            throws ZserioExtensionException
    {
        if (!(typeInstantiation.getBaseType() instanceof IntegerType))
            return null;

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
        final String bitFieldLength = getDynamicBitFieldLength(typeInstantiation, cppExpressionFormatter);
        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(typeInstantiation);
        final IntegerType typeToCheck = (IntegerType)typeInstantiation.getBaseType();

        final BigInteger zserioLowerBound = typeToCheck.getLowerBound(typeInstantiation);
        final BigInteger zserioUpperBound = typeToCheck.getUpperBound(typeInstantiation);
        boolean checkLowerBound = true;
        boolean checkUpperBound = true;

        if (bitFieldLength == null)
        {
            final BigInteger nativeLowerBound = nativeType.getLowerBound();
            checkLowerBound = nativeLowerBound.compareTo(zserioLowerBound) < 0;

            final BigInteger nativeUpperBound = nativeType.getUpperBound();
            checkUpperBound = nativeUpperBound.compareTo(zserioUpperBound) > 0;
            final boolean hasFullRange = !checkLowerBound && !checkUpperBound;
            if (hasFullRange)
                return null;
        }

        final String lowerBound = nativeType.formatLiteral(zserioLowerBound);
        final String upperBound = nativeType.formatLiteral(zserioUpperBound);
        final NativeIntegralTypeInfoTemplateData typeInfo =
                new NativeIntegralTypeInfoTemplateData(nativeType, typeInstantiation);

        return new IntegerRange(checkLowerBound, lowerBound, upperBound, typeInfo, bitFieldLength);
    }

    private static String getDynamicBitFieldLength(TypeInstantiation instantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        if (!(instantiation instanceof DynamicBitFieldInstantiation))
            return null;

        final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                (DynamicBitFieldInstantiation)instantiation;
        return cppExpressionFormatter.formatGetter(dynamicBitFieldInstantiation.getLengthExpression());
    }

    private static String createAlignmentValue(TemplateDataContext context, Field field,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
        return cppExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(TemplateDataContext context, Field field,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
        return cppExpressionFormatter.formatGetter(initializerExpression);
    }

    private static Constraint createConstraint(TemplateDataContext context, Field field,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        final CppConstraintExpressionFormattingPolicy expressionFormattingPolicy =
                new CppConstraintExpressionFormattingPolicy(context, includeCollector, field);
        final ExpressionFormatter cppConstaintExpressionFormatter =
                new ExpressionFormatter(expressionFormattingPolicy);

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
        return new Constraint(constraintExpression, cppExpressionFormatter, cppConstaintExpressionFormatter);
    }

    private static Offset createOffset(TemplateDataContext context, Field field,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(context, offsetExpression, includeCollector);
    }

    private static Array createArray(TemplateDataContext context, CppNativeType cppNativeType,
            TypeInstantiation typeInstantiation, CompoundType parentType, IncludeCollector includeCollector)
            throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof ArrayInstantiation))
            return null;

        if (!(cppNativeType instanceof NativeArrayType))
        {
            throw new ZserioExtensionException("Inconsistent instantiation '" +
                    typeInstantiation.getClass().getName() + "' and native type '" +
                    cppNativeType.getClass().getName() + "'!");
        }

        return new Array(context, (NativeArrayType)cppNativeType, (ArrayInstantiation)typeInstantiation,
                parentType, includeCollector);
    }

    private static Compound createCompound(TemplateDataContext context, TypeInstantiation typeInstantiation,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(context, (ParameterizedTypeInstantiation)typeInstantiation, includeCollector);
        }
        if (typeInstantiation.getBaseType() instanceof CompoundType)
        {
            return new Compound(context, (CompoundType)typeInstantiation.getBaseType(), includeCollector);
        }
        else
        {
            return null;
        }
    }

    private static boolean usesSharedPointer(TemplateDataContext context, Field field,
            ZserioType fieldBaseType) throws ZserioExtensionException
    {
        if (!context.getParameterFieldsCollector().isUsedAsParameter(field) ||
                !(fieldBaseType instanceof CompoundType))
        {
            return false;
        }

        return isAboveTreshold(context, (CompoundType)fieldBaseType);
    }

    static boolean isAboveTreshold(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        return isAboveTreshold(context, compoundType, 0);
    }

    private static boolean isAboveTreshold(TemplateDataContext context, CompoundType compoundType, int numBytes)
            throws ZserioExtensionException
    {
        if (!compoundType.getTypeParameters().isEmpty())
            return true; // parameterized type TODO[Mi-L@]

        for (Field field : compoundType.getFields())
        {
            TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            if (fieldTypeInstantiation instanceof ArrayInstantiation)
                return true; // array field

            ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            if (fieldBaseType instanceof CompoundType)
            {
                if (fieldBaseType == compoundType)
                    return true; // recursion
                return isAboveTreshold(context, (CompoundType)fieldBaseType, numBytes);
            }
            else if (fieldBaseType instanceof EnumType)
            {
                fieldTypeInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
                fieldBaseType = fieldTypeInstantiation.getBaseType();
            }
            else if (fieldBaseType instanceof BitmaskType)
            {
                fieldTypeInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();
                fieldBaseType = fieldTypeInstantiation.getBaseType();
            }

            if (fieldBaseType instanceof IntegerType)
            {
                final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
                final NativeIntegralType nativeIntegralType =
                        cppNativeMapper.getCppIntegralType(fieldTypeInstantiation);
                numBytes += nativeIntegralType.getNumBits() / Byte.SIZE;
            }
            else
            {
                return true; // type which doesn't have fixed size of the mapped native type
            }

            if (numBytes > context.getCompoundParameterTreshold())
                return true;
        }

        return false;
    }

    private final Optional optional;
    private final Compound compound;
    private final String name;
    private final boolean usesSharedPointer;
    private final NativeTypeInfoTemplateData typeInfo;
    private final String getterName;
    private final String setterName;
    private final String readerName;
    private final boolean isExtended;
    private final String isPresentIndicatorName;
    private final boolean isPackable;
    private final IntegerRange integerRange;
    private final String alignmentValue;
    private final String initializer;
    private final boolean usesAnyHolder;
    private final boolean needsAllocator;
    private final boolean holderNeedsAllocator;
    private final Constraint constraint;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final BitSizeTemplateData bitSize;
    private final DocCommentsTemplateData docComments;
}
