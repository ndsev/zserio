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

public class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(CppNativeMapper cppNativeMapper, CompoundType parentType,
            Field field, ExpressionFormatter cppExpressionFormatter,
            ExpressionFormatter cppIndirectExpressionFormatter, IncludeCollector includeCollector,
            boolean withWriterCode, boolean withRangeCheckCode) throws ZserioEmitException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final TypeReference fieldTypeReference = fieldTypeInstantiation.getTypeReference();
        final ZserioType fieldType = fieldTypeReference.getType();
        final ZserioType fieldBaseType = fieldTypeReference.getBaseTypeReference().getType();

        final CppNativeType fieldNativeType = cppNativeMapper.getCppType(fieldTypeReference);
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

        integerRange = createIntegerRange(cppNativeMapper, fieldType, cppExpressionFormatter);

        alignmentValue = createAlignmentValue(field, cppExpressionFormatter);
        initializer = createInitializer(field, cppExpressionFormatter);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isSimpleType = fieldNativeType.isSimpleType();
        isEnum = fieldBaseType instanceof EnumType;

        constraint = createConstraint(field, cppNativeMapper, cppExpressionFormatter, includeCollector);

        offset = createOffset(field, cppNativeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
        array = createArray(fieldNativeType, fieldBaseType, parentType, cppNativeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, includeCollector, withWriterCode);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(fieldBaseType, cppExpressionFormatter);
        bitSizeValue = createBitSizeValue(fieldBaseType, cppExpressionFormatter);
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
        public Optional(Expression optionalClauseExpression, String resetterName, String indicatorName,
                ExpressionFormatter cppExpressionFormatter, boolean isRecursive) throws ZserioEmitException
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
                TypeInstantiation compoundFieldInstantiation, boolean withWriterCode) throws ZserioEmitException
        {
            // TODO[Mi-L@][typeref] ParameterizedTypeInstantiation could hold the base compound type.
            final TypeReference baseTypeReference =
                    compoundFieldInstantiation.getTypeReference().getBaseTypeReference();
            final CompoundType baseType = (CompoundType)baseTypeReference.getType();
            final List<InstantiatedParameter> parameters =
                    compoundFieldInstantiation.getInstantiatedParameters();
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
        public Offset(Expression offsetExpression, CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter)
                        throws ZserioEmitException
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
        public IntegerRange(CppNativeMapper cppNativeMapper, IntegerType typeToCheck,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            bitFieldLength = !(typeToCheck instanceof BitFieldType) ? null :
                    cppExpressionFormatter.formatGetter(((BitFieldType)typeToCheck).getLengthExpression());

            final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(typeToCheck);
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
        public Array(NativeArrayType nativeType, ArrayType arrayType, CompoundType parentType,
                CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, IncludeCollector includeCollector,
                boolean withWriterCode) throws ZserioEmitException
        {
            final TypeInstantiation elementTypeInstantiation = arrayType.getElementTypeInstantiation();
            final TypeReference elementTypeReference = elementTypeInstantiation.getTypeReference();
            final ZserioType elementBaseType = elementTypeReference.getBaseTypeReference().getType();

            traitsName = nativeType.getArrayTraitsName();
            hasTemplatedTraits = nativeType.hasTemplatedTraits();
            isImplicit = arrayType.isImplicit();
            length = createLength(arrayType, cppExpressionFormatter);
            final CppNativeType elementNativeType = cppNativeMapper.getCppType(elementTypeReference);
            elementCppTypeName = elementNativeType.getFullName();
            includeCollector.addHeaderIncludesForType(elementNativeType);
            requiresElementFactory = nativeType.requiresElementFactory();
            elementBitSizeValue = nativeType.requiresElementBitSize()
                    ? createBitSizeValue(elementBaseType, cppExpressionFormatter)
                    : null;
            elementCompound = createCompound(cppNativeMapper, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, parentType, elementTypeInstantiation, withWriterCode);
            elementIntegerRange = createIntegerRange(cppNativeMapper, elementBaseType,
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
            ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        final boolean isRecursive = baseFieldType == parentType;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String resetterName = AccessorNameFormatter.getResetterName(field);
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, resetterName, indicatorName, cppExpressionFormatter,
                isRecursive);
    }

    private static IntegerRange createIntegerRange(CppNativeMapper cppNativeMapper,
            ZserioType typeToCheck, ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        if (!(typeToCheck instanceof IntegerType))
            return null;

        return new IntegerRange(cppNativeMapper, (IntegerType)typeToCheck, cppExpressionFormatter);
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

    private static Constraint createConstraint(Field field, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioEmitException
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
                    throws ZserioEmitException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, cppNativeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
    }

    private static Array createArray(CppNativeType cppNativeType, ZserioType baseType,
            CompoundType parentType, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioEmitException
    {
        if (!(baseType instanceof ArrayType))
            return null;

        if (!(cppNativeType instanceof NativeArrayType))
            throw new ZserioEmitException("Inconsistent base type '" + baseType.getClass() +
                    "' and native type '" + cppNativeType.getClass() + "'!");

        return new Array((NativeArrayType)cppNativeType, (ArrayType)baseType, parentType, cppNativeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, includeCollector, withWriterCode);
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

    private static Compound createCompound(CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            CompoundType owner, TypeInstantiation fieldTypeInstantiation, boolean withWriterCode)
                    throws ZserioEmitException
    {
        if (fieldTypeInstantiation.getTypeReference().getBaseTypeReference().getType() instanceof CompoundType)
        {
            return new Compound(cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    owner, fieldTypeInstantiation, withWriterCode);
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
    private final boolean isEnum;
    private final Constraint constraint;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String bitSizeValue;
    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
