package zserio.emit.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.EnumType;
import zserio.ast.UnionType;
import zserio.ast.BitFieldType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeInstantiation.InstantiatedParameter;
import zserio.ast.TypeReference;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeArrayType;
import zserio.emit.cpp.types.NativeIntegralType;
import zserio.emit.cpp.types.NativeOptionalHolderType;

public class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(CppNativeTypeMapper cppNativeTypeMapper, CompoundType parentType,
            Field field, ExpressionFormatter cppExpressionFormatter,
            ExpressionFormatter cppIndirectExpressionFormatter, IncludeCollector includeCollector,
            boolean withWriterCode, boolean withRangeCheckCode) throws ZserioEmitException
    {
        final ZserioType fieldType = TypeReference.resolveType(field.getFieldType());
        final ZserioType baseFieldType = TypeReference.resolveBaseType(fieldType);

        CppNativeType fieldNativeType = cppNativeTypeMapper.getCppType(fieldType);
        includeCollector.addHeaderIncludesForType(fieldNativeType);
        if (field.getIsOptional())
        {
            optional = createOptional(field, baseFieldType, parentType, fieldNativeType,
                    cppExpressionFormatter);
            fieldNativeType = cppNativeTypeMapper.getCppOptionalHolderType(fieldType);
            includeCollector.addHeaderIncludesForType(fieldNativeType);
        }
        else
        {
            optional = null;
        }

        compound = createCompound(cppNativeTypeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                parentType, baseFieldType, withWriterCode);

        name = field.getName();
        cppTypeName = fieldNativeType.getFullName();
        cppArgumentTypeName = fieldNativeType.getArgumentTypeName();

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);
        readerName = AccessorNameFormatter.getReaderName(field);

        integerRange = createIntegerRange(cppNativeTypeMapper, fieldType, cppExpressionFormatter);

        alignmentValue = createAlignmentValue(field, cppExpressionFormatter);
        initializer = createInitializer(field, cppExpressionFormatter);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isSimpleType = fieldNativeType.isSimpleType();
        isEnum = baseFieldType instanceof EnumType;

        constraint = createConstraint(field, cppNativeTypeMapper, cppExpressionFormatter, includeCollector);

        offset = createOffset(field, cppNativeTypeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
        array = createArray(fieldNativeType, baseFieldType, parentType, cppNativeTypeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, withWriterCode);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(baseFieldType, cppExpressionFormatter);
        bitSizeValue = createBitSizeValue(baseFieldType, cppExpressionFormatter);
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

    public String getBitSizeValue()
    {
        return bitSizeValue;
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
        public Optional(Expression optionalClauseExpression, String indicatorName,
                ExpressionFormatter cppExpressionFormatter, CppNativeType fieldNativeType, boolean isRecursive)
                        throws ZserioEmitException
        {
            clause = (optionalClauseExpression == null) ? null :
                cppExpressionFormatter.formatGetter(optionalClauseExpression);
            this.indicatorName = indicatorName;
            cppRawTypeName = fieldNativeType.getFullName();
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

        public String getCppRawTypeName()
        {
            return cppRawTypeName;
        }

        public boolean getIsRecursive()
        {
            return isRecursive;
        }

        private final String clause;
        private final String indicatorName;
        private final String cppRawTypeName;
        private final boolean isRecursive;
    }

    public static class Compound
    {
        public Compound(CppNativeTypeMapper cppNativeTypeMapper, CompoundType owner,
                CompoundType compoundFieldType, boolean withWriterCode)
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(0);
            needsChildrenInitialization = compoundFieldType.needsChildrenInitialization();
        }

        public Compound(CppNativeTypeMapper cppNativeTypeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, CompoundType owner,
                TypeInstantiation compoundFieldType, boolean withWriterCode) throws ZserioEmitException
        {
            final CompoundType baseType = compoundFieldType.getBaseType();
            final List<InstantiatedParameter> parameters = compoundFieldType.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
            {
                instantiatedParameters.add(new InstantiatedParameterData(cppExpressionFormatter,
                        cppIndirectExpressionFormatter, parameter));
            }

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
            public InstantiatedParameterData(ExpressionFormatter cppExpressionFormatter,
                    ExpressionFormatter cppIndirectExpressionFormatter,
                    InstantiatedParameter instantiatedParameter) throws ZserioEmitException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                expression = cppExpressionFormatter.formatGetter(argumentExpression);
                indirectExpression = cppIndirectExpressionFormatter.formatGetter(argumentExpression);
            }

            public String getExpression()
            {
                return expression;
            }

            public String getIndirectExpression()
            {
                return indirectExpression;
            }

            private final String expression;
            private final String indirectExpression;
        }

        private final ArrayList<InstantiatedParameterData> instantiatedParameters;
        private final boolean needsChildrenInitialization;
    }

    public static class Constraint
    {
        public Constraint(Expression constraintExpression, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppConstraintExpressionFormatter) throws ZserioEmitException
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
        public Offset(Expression offsetExpression, CppNativeTypeMapper cppNativeTypeMapper,
                ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter)
                        throws ZserioEmitException
        {
            getter = cppExpressionFormatter.formatGetter(offsetExpression);
            indirectGetter = cppIndirectExpressionFormatter.formatGetter(offsetExpression);
            setter = cppExpressionFormatter.formatSetter(offsetExpression);
            indirectSetter = cppIndirectExpressionFormatter.formatSetter(offsetExpression);
            typeName = cppNativeTypeMapper.getCppType(offsetExpression.getExprZserioType()).getFullName();
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
        public IntegerRange(CppNativeTypeMapper cppNativeTypeMapper, IntegerType typeToCheck,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            bitFieldLength = !(typeToCheck instanceof BitFieldType) ? null :
                    cppExpressionFormatter.formatGetter(((BitFieldType)typeToCheck).getLengthExpression());

            final NativeIntegralType nativeType = cppNativeTypeMapper.getCppIntegralType(typeToCheck);
            final BigInteger zserioLowerBound = typeToCheck.getLowerBound();
            final BigInteger nativeLowerBound = nativeType.getLowerBound();
            checkLowerBound = zserioLowerBound == null || nativeLowerBound.compareTo(zserioLowerBound) < 0;

            final BigInteger zserioUpperBound = typeToCheck.getUpperBound();
            final BigInteger nativeUpperBound = nativeType.getUpperBound();
            final boolean checkUpperBound = zserioUpperBound == null ||
                    nativeUpperBound.compareTo(zserioUpperBound) > 0;

            // Zserio types that have the same bounds as their native type are not checked
            hasFullRange = !checkLowerBound && !checkUpperBound;

            lowerBound = zserioLowerBound != null ? nativeType.formatLiteral(zserioLowerBound) : null;
            upperBound = zserioUpperBound != null ? nativeType.formatLiteral(zserioUpperBound) : null;
        }

        public String getBitFieldLength()
        {
            return bitFieldLength;
        }

        public boolean getHasFullRange()
        {
            return hasFullRange;
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

        private final String bitFieldLength;
        private final boolean hasFullRange;
        private final boolean checkLowerBound;
        private final String lowerBound;
        private final String upperBound;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayType baseType, CompoundType parentType,
                CppNativeTypeMapper cppNativeTypeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, boolean withWriterCode)
                        throws ZserioEmitException
        {
            final ZserioType elementType = TypeReference.resolveBaseType(baseType.getElementType());

            traitsName = nativeType.getArrayTraitsName();
            hasTemplatedTraits = nativeType.hasTemplatedTraits();
            isImplicit = baseType.isImplicit();
            length = createLength(baseType, cppExpressionFormatter);
            elementCppTypeName = cppNativeTypeMapper.getCppType(elementType).getFullName();
            requiresElementFactory = nativeType.requiresElementFactory();
            elementBitSizeValue = nativeType.requiresElementBitSize()
                    ? createBitSizeValue(elementType, cppExpressionFormatter)
                    : null;
            elementCompound = createCompound(cppNativeTypeMapper, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, parentType, elementType, withWriterCode);
            elementIntegerRange = createIntegerRange(cppNativeTypeMapper, elementType, cppExpressionFormatter);
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

        public String getElementBitSizeValue()
        {
            return elementBitSizeValue;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        public IntegerRange getElementIntegerRange()
        {
            return elementIntegerRange;
        }

        private static String createLength(ArrayType arrayType, ExpressionFormatter cppExpressionFormatter)
                throws ZserioEmitException
        {
            final Expression lengthExpression = arrayType.getLengthExpression();
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
        private final String elementBitSizeValue;
        private final Compound elementCompound;
        private final IntegerRange elementIntegerRange;
    }

    private static Optional createOptional(Field field, ZserioType baseFieldType, CompoundType parentType,
            CppNativeType fieldNativeType, ExpressionFormatter cppExpressionFormatter)
                    throws ZserioEmitException
    {
        ZserioType fieldInstantiatedType = baseFieldType;
        if (baseFieldType instanceof TypeInstantiation)
            fieldInstantiatedType = ((TypeInstantiation)baseFieldType).getBaseType();
        final boolean isRecursive = fieldInstantiatedType == parentType;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, indicatorName, cppExpressionFormatter, fieldNativeType,
                isRecursive);
    }

    private static IntegerRange createIntegerRange(CppNativeTypeMapper cppNativeTypeMapper,
            ZserioType typeToCheck, ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        if (!(typeToCheck instanceof IntegerType))
            return null;

        return new IntegerRange(cppNativeTypeMapper, (IntegerType)typeToCheck, cppExpressionFormatter);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return cppExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return cppExpressionFormatter.formatGetter(initializerExpression);
    }

    private static Constraint createConstraint(Field field, CppNativeTypeMapper cppNativeTypeMapper,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioEmitException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        final CppConstraintExpressionFormattingPolicy expressionFormattingPolicy =
                new CppConstraintExpressionFormattingPolicy(cppNativeTypeMapper, includeCollector, field);
        final ExpressionFormatter cppConstaintExpressionFormatter =
                new ExpressionFormatter(expressionFormattingPolicy);

        return new Constraint(constraintExpression, cppExpressionFormatter, cppConstaintExpressionFormatter);
    }

    private static Offset createOffset(Field field, CppNativeTypeMapper cppNativeTypeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter)
                    throws ZserioEmitException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, cppNativeTypeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
    }

    private static Array createArray(CppNativeType cppNativeType, ZserioType baseType,
            CompoundType parentType, CppNativeTypeMapper cppNativeTypeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            boolean withWriterCode) throws ZserioEmitException
    {
        if (!(baseType instanceof ArrayType))
            return null;

        if (cppNativeType instanceof NativeOptionalHolderType)
            cppNativeType = ((NativeOptionalHolderType)cppNativeType).getWrappedType();

        if (!(cppNativeType instanceof NativeArrayType))
            throw new ZserioEmitException("Inconsistent base type '" + baseType.getClass() +
                    "' and native type '" + cppNativeType.getClass() + "'!");

        return new Array((NativeArrayType)cppNativeType, (ArrayType)baseType, parentType, cppNativeTypeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, withWriterCode);
    }

    static String createBitSizeValue(ZserioType baseFieldType, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        String value;
        if (baseFieldType instanceof FixedSizeType)
        {
            value = CppLiteralFormatter.formatUInt8Literal(((FixedSizeType)baseFieldType).getBitSize());
        }
        else if (baseFieldType instanceof BitFieldType)
        {
            final BitFieldType bitFieldType = (BitFieldType)baseFieldType;
            final Integer bitSize = bitFieldType.getBitSize();
            if (bitSize != null)
                value = CppLiteralFormatter.formatUInt8Literal(bitSize);
            else
                value = cppExpressionFormatter.formatGetter(bitFieldType.getLengthExpression());
        }
        else
        {
            value = null;
        }

        return value;
    }

    private static Compound createCompound(CppNativeTypeMapper cppNativeTypeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            CompoundType owner, ZserioType baseFieldType, boolean withWriterCode) throws ZserioEmitException
    {
        if (baseFieldType instanceof CompoundType)
            return new Compound(cppNativeTypeMapper, owner, (CompoundType)baseFieldType, withWriterCode);
        else if (baseFieldType instanceof TypeInstantiation)
            return new Compound(cppNativeTypeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    owner, (TypeInstantiation)baseFieldType, withWriterCode);
        else
            return null;
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
    private final boolean isEnum;
    private final Constraint constraint;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String bitSizeValue;
    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
