package zserio.extension.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeArrayTraits;
import zserio.extension.cpp.types.NativeArrayType;
import zserio.extension.cpp.types.NativeIntegralType;

public class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(CppNativeMapper cppNativeMapper, CompoundType parentType,
            Field field, ExpressionFormatter cppExpressionFormatter,
            ExpressionFormatter cppIndirectExpressionFormatter, IncludeCollector includeCollector,
            boolean withWriterCode, boolean withRangeCheckCode) throws ZserioExtensionException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();

        final CppNativeType fieldNativeType = cppNativeMapper.getCppType(fieldTypeInstantiation);
        includeCollector.addHeaderIncludesForType(fieldNativeType);

        optional = (field.isOptional()) ?
                createOptional(field, fieldBaseType, parentType, cppExpressionFormatter) : null;
        compound = createCompound(cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                parentType, fieldTypeInstantiation, withWriterCode);

        name = field.getName();
        cppTypeName = fieldNativeType.getFullName();
        cppArgumentTypeName = fieldNativeType.getArgumentTypeName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);
        readerName = AccessorNameFormatter.getReaderName(field);

        integerRange = createIntegerRange(cppNativeMapper, fieldTypeInstantiation, cppExpressionFormatter);

        alignmentValue = createAlignmentValue(field, cppExpressionFormatter);
        initializer = createInitializer(field, cppExpressionFormatter);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isSimpleType = fieldNativeType.isSimpleType();
        needsAllocator = !isSimpleType;
        holderNeedsAllocator = usesAnyHolder || (optional != null && optional.getIsRecursive());
        isEnum = fieldBaseType instanceof EnumType;

        constraint = createConstraint(field, cppNativeMapper, cppExpressionFormatter, includeCollector);
        offset = createOffset(field, cppNativeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
        array = createArray(fieldNativeType, fieldTypeInstantiation, parentType, cppNativeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, includeCollector, withWriterCode);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(fieldTypeInstantiation,
                cppExpressionFormatter);
        bitSize = createBitSize(fieldTypeInstantiation, cppExpressionFormatter);
        this.withWriterCode = withWriterCode;
        this.withRangeCheckCode = withRangeCheckCode;
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

    public String getCppTypeName()
    {
        return cppTypeName;
    }

    public String getCppArgumentTypeName()
    {
        return cppArgumentTypeName;
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

    public boolean getIsSimpleType()
    {
        return isSimpleType;
    }

    public boolean getNeedsAllocator()
    {
        return needsAllocator;
    }

    public boolean getHolderNeedsAllocator()
    {
        return holderNeedsAllocator;
    }

    public boolean getIsEnum()
    {
        return isEnum;
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

    public BitSize getBitSize()
    {
        return bitSize;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public static class Optional
    {
        public Optional(Expression optionalClauseExpression, String resetterName, String indicatorName,
                ExpressionFormatter cppExpressionFormatter, boolean isRecursive) throws ZserioExtensionException
        {
            clause = (optionalClauseExpression == null) ? null :
                cppExpressionFormatter.formatGetter(optionalClauseExpression);
            this.resetterName = resetterName;
            this.indicatorName = indicatorName;
            this.isRecursive = isRecursive;
        }

        public String getClause()
        {
            return clause;
        }

        public String getResetterName()
        {
            return resetterName;
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
        private final String resetterName;
        private final String indicatorName;
        private final boolean isRecursive;
    }

    public static class Compound
    {
        public Compound(CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, CompoundType owner,
                ParameterizedTypeInstantiation parameterizedTypeInstantiation,
                boolean withWriterCode) throws ZserioExtensionException
        {
            final CompoundType baseType = parameterizedTypeInstantiation.getBaseType();
            final List<InstantiatedParameter> parameters =
                    parameterizedTypeInstantiation.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
            {
                instantiatedParameters.add(new InstantiatedParameterData(cppNativeMapper,
                        cppExpressionFormatter, cppIndirectExpressionFormatter, parameter));
            }

            needsChildrenInitialization = baseType.needsChildrenInitialization();
        }

        public Compound(TypeInstantiation typeInstantiation)
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>();
            final CompoundType baseType = (CompoundType)typeInstantiation.getBaseType();
            needsChildrenInitialization = baseType.needsChildrenInitialization();
        }

        public Iterable<InstantiatedParameterData> getInstantiatedParameters()
        {
            return instantiatedParameters;
        }

        public boolean getNeedsChildrenInitialization()
        {
            return needsChildrenInitialization;
        }

        public static class InstantiatedParameterData
        {
            public InstantiatedParameterData(CppNativeMapper cppNativeMapper,
                    ExpressionFormatter cppExpressionFormatter,
                    ExpressionFormatter cppIndirectExpressionFormatter,
                    InstantiatedParameter instantiatedParameter) throws ZserioExtensionException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                expression = cppExpressionFormatter.formatGetter(argumentExpression);
                indirectExpression = cppIndirectExpressionFormatter.formatGetter(argumentExpression);
                final CppNativeType cppNativeType = cppNativeMapper.getCppType(
                        instantiatedParameter.getParameter().getTypeReference());
                cppTypeName = cppNativeType.getFullName();
                isSimpleType = cppNativeType.isSimpleType();
            }

            public String getExpression()
            {
                return expression;
            }

            public String getIndirectExpression()
            {
                return indirectExpression;
            }

            public String getCppTypeName()
            {
                return cppTypeName;
            }

            public boolean getIsSimpleType()
            {
                return isSimpleType;
            }

            private final String expression;
            private final String indirectExpression;
            private final String cppTypeName;
            private final boolean isSimpleType;
        }

        private final ArrayList<InstantiatedParameterData> instantiatedParameters;
        private final boolean needsChildrenInitialization;
    }

    public static class Constraint
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

    public static class Offset
    {
        public Offset(Expression offsetExpression, CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter)
                        throws ZserioExtensionException
        {
            getter = cppExpressionFormatter.formatGetter(offsetExpression);
            indirectGetter = cppIndirectExpressionFormatter.formatGetter(offsetExpression);
            setter = cppExpressionFormatter.formatSetter(offsetExpression);
            indirectSetter = cppIndirectExpressionFormatter.formatSetter(offsetExpression);
            typeName = cppNativeMapper.getCppType(offsetExpression.getExprZserioType()).getFullName();
            containsIndex = offsetExpression.containsIndex();
        }

        public boolean getContainsIndex()
        {
            return containsIndex;
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

        public String getTypeName()
        {
            return typeName;
        }

        private final String getter;
        private final String indirectGetter;
        private final String setter;
        private final String indirectSetter;
        private final String typeName;
        private final boolean containsIndex;
    }

    public static class IntegerRange
    {
        public IntegerRange(boolean checkLowerBound, String lowerBound, String upperBound,
                String bitFieldLength, boolean isSigned) throws ZserioExtensionException
        {
            this.checkLowerBound = checkLowerBound;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.bitFieldLength = bitFieldLength;
            this.isSigned = isSigned;
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

        public String getBitFieldLength()
        {
            return bitFieldLength;
        }

        public boolean getIsSigned()
        {
            return isSigned;
        }

        private final String bitFieldLength;
        private final boolean isSigned;
        private final boolean checkLowerBound;
        private final String lowerBound;
        private final String upperBound;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayInstantiation arrayInstantiation,
                CompoundType parentType, CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
                IncludeCollector includeCollector, boolean withWriterCode) throws ZserioExtensionException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();

            final NativeArrayTraits arrayTraits = nativeType.getArrayTraits();
            traitsName = arrayTraits.getName();
            hasTemplatedTraits = arrayTraits.isTemplated();
            isImplicit = arrayInstantiation.isImplicit();
            if (arrayInstantiation.isPacked())
            {
                ZserioToolPrinter.printWarning(arrayInstantiation.getLocation(),
                        "Unimplemented packed array field reached!");
            }
            length = createLength(arrayInstantiation, cppExpressionFormatter);
            final CppNativeType elementNativeType = cppNativeMapper.getCppType(elementTypeInstantiation);
            elementCppTypeName = elementNativeType.getFullName();
            includeCollector.addHeaderIncludesForType(elementNativeType);
            requiresElementFactory = nativeType.getArrayTraits().requiresElementFactory();
            elementBitSize = nativeType.getArrayTraits().requiresElementBitSize()
                    ? createBitSize(elementTypeInstantiation, cppExpressionFormatter)
                    : null;
            elementCompound = createCompound(cppNativeMapper, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, parentType, elementTypeInstantiation, withWriterCode);
            elementIntegerRange = createIntegerRange(cppNativeMapper, elementTypeInstantiation,
                    cppExpressionFormatter);
        }

        public String getTraitsName()
        {
            return traitsName;
        }

        public boolean getHasTemplatedTraits()
        {
            return hasTemplatedTraits;
        }

        public boolean getIsImplicit()
        {
            return isImplicit;
        }

        public String getLength()
        {
            return length;
        }

        public String getElementCppTypeName()
        {
            return elementCppTypeName;
        }

        public boolean getRequiresElementFactory()
        {
            return requiresElementFactory;
        }

        public BitSize getElementBitSize()
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

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return cppExpressionFormatter.formatGetter(lengthExpression);
        }

        private final String traitsName;
        private final boolean hasTemplatedTraits;
        private final boolean isImplicit;
        private final String length;
        private final String elementCppTypeName;
        private final boolean requiresElementFactory;
        private final BitSize elementBitSize;
        private final Compound elementCompound;
        private final IntegerRange elementIntegerRange;
    }

    public static class BitSize
    {
        public BitSize(String value, boolean isDynamicBitField)
        {
            this.value = value;
            this.isDynamicBitField = isDynamicBitField;
        }

        public String getValue()
        {
            return value;
        }

        public boolean getIsDynamicBitField()
        {
            return isDynamicBitField;
        }

        private final String value;
        private final boolean isDynamicBitField;
    }

    private static Optional createOptional(Field field, ZserioType baseFieldType, CompoundType parentType,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        final boolean isRecursive = baseFieldType == parentType;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String resetterName = AccessorNameFormatter.getResetterName(field);
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, resetterName, indicatorName, cppExpressionFormatter,
                isRecursive);
    }

    private static IntegerRange createIntegerRange(CppNativeMapper cppNativeMapper,
            TypeInstantiation typeInstantiation, ExpressionFormatter cppExpressionFormatter)
                    throws ZserioExtensionException
    {
        if (!(typeInstantiation.getBaseType() instanceof IntegerType))
            return null;

        final String bitFieldLength = getDynamicBitFieldLength(typeInstantiation, cppExpressionFormatter);
        final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(typeInstantiation);
        final boolean isSigned = nativeType.isSigned();
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

        return new IntegerRange(checkLowerBound, lowerBound, upperBound, bitFieldLength, isSigned);
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

    private static String createAlignmentValue(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return cppExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        if (initializerExpression.getStringValue() != null)
        {
            return CppLiteralFormatter.formatStringLiteral(initializerExpression.getStringValue());
        }
        else
        {
            return cppExpressionFormatter.formatGetter(initializerExpression);
        }
    }

    private static Constraint createConstraint(Field field, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        final CppConstraintExpressionFormattingPolicy expressionFormattingPolicy =
                new CppConstraintExpressionFormattingPolicy(cppNativeMapper, includeCollector, field);
        final ExpressionFormatter cppConstaintExpressionFormatter =
                new ExpressionFormatter(expressionFormattingPolicy);

        return new Constraint(constraintExpression, cppExpressionFormatter, cppConstaintExpressionFormatter);
    }

    private static Offset createOffset(Field field, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter)
                    throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, cppNativeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
    }

    private static Array createArray(CppNativeType cppNativeType, TypeInstantiation typeInstantiation,
            CompoundType parentType, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof ArrayInstantiation))
            return null;

        if (!(cppNativeType instanceof NativeArrayType))
        {
            throw new ZserioExtensionException("Inconsistent instantiation '" +
                    typeInstantiation.getClass().getName() + "' and native type '" +
                    cppNativeType.getClass().getName() + "'!");
        }

        return new Array((NativeArrayType)cppNativeType, (ArrayInstantiation)typeInstantiation, parentType,
                cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter, includeCollector,
                withWriterCode);
    }

    private static BitSize createBitSize(TypeInstantiation instantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        final ZserioType baseInstantiationType = instantiation.getBaseType();
        BitSize bitSize;
        if (baseInstantiationType instanceof FixedSizeType)
        {
            final String value = CppLiteralFormatter.formatUInt8Literal(
                    ((FixedSizeType)baseInstantiationType).getBitSize());
            bitSize = new BitSize(value, false);
        }
        else if (instantiation instanceof DynamicBitFieldInstantiation)
        {
            final String value = cppExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)instantiation).getLengthExpression());
            bitSize = new BitSize(value, true);
        }
        else
        {
            bitSize = null;
        }

        return bitSize;
    }

    private static Compound createCompound(CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            CompoundType owner, TypeInstantiation typeInstantiation, boolean withWriterCode)
                    throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    owner, (ParameterizedTypeInstantiation)typeInstantiation, withWriterCode);
        }
        if (typeInstantiation.getBaseType() instanceof CompoundType)
        {
            return new Compound(typeInstantiation);
        }
        else
        {
            return null;
        }
    }

    private final Optional optional;
    private final Compound compound;
    private final String name;
    private final String cppTypeName;
    private final String cppArgumentTypeName;
    private final String getterName;
    private final String setterName;
    private final String readerName;
    private final IntegerRange integerRange;
    private final String alignmentValue;
    private final String initializer;
    private final boolean usesAnyHolder;
    private final boolean isSimpleType;
    private final boolean needsAllocator;
    private final boolean holderNeedsAllocator;
    private final boolean isEnum;
    private final Constraint constraint;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final BitSize bitSize;
    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
