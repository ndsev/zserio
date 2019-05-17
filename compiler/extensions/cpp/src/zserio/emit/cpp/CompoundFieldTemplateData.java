package zserio.emit.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.UnionType;
import zserio.ast.BitFieldType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
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
import zserio.emit.cpp.types.NativeEnumType;
import zserio.emit.cpp.types.NativeIntegralType;
import zserio.emit.cpp.types.NativeOptionalHolderType;

public class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(CppNativeTypeMapper cppNativeTypeMapper,
            CompoundType parentType, Field field,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioEmitException
    {
        final ZserioType fieldType = TypeReference.resolveType(field.getFieldType());
        final ZserioType baseFieldType = TypeReference.resolveBaseType(fieldType);
        optional = createOptional(field, cppExpressionFormatter);
        compound = createCompound(cppNativeTypeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                parentType, baseFieldType, withWriterCode);

        final CppNativeType fieldNativeType = cppNativeTypeMapper.getCppType(fieldType);
        includeCollector.addHeaderIncludesForType(fieldNativeType);

        name = field.getName();
        cppTypeName = fieldNativeType.getFullName();
        cppArgumentTypeName = fieldNativeType.getArgumentTypeName();
        zserioTypeName = ZserioTypeUtil.getFullName(fieldType);

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        integerRange = createIntegerRange(cppNativeTypeMapper, fieldType, cppExpressionFormatter);

        alignmentValue = createAlignmentValue(field, cppExpressionFormatter);
        initializer = createInitializer(field, cppExpressionFormatter);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isSimpleType = fieldNativeType.isSimpleType();
        isEnum = fieldNativeType instanceof NativeEnumType;

        constraint = createConstraint(field, cppExpressionFormatter);

        offset = createOffset(field, cppNativeTypeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
        array = createArray(fieldNativeType, baseFieldType, parentType, cppNativeTypeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, withWriterCode);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(baseFieldType, cppExpressionFormatter);
        bitSizeValue = createBitSizeValue(baseFieldType, cppExpressionFormatter);
        final boolean isOptionalField = (optional != null);
        optionalHolder = createOptionalHolder(fieldType, baseFieldType, parentType, isOptionalField,
                cppNativeTypeMapper, includeCollector);
        this.withWriterCode = withWriterCode;
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

    public String getZserioTypeName()
    {
        return zserioTypeName;
    }

    public String getGetterName()
    {
        return getterName;
    }

    public String getSetterName()
    {
        return setterName;
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

    public String getConstraint()
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

    public OptionalHolder getOptionalHolder()
    {
        return optionalHolder;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public static class Optional
    {
        public Optional(Expression optionalClauseExpression, String indicatorName,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            clause = (optionalClauseExpression == null) ? null :
                cppExpressionFormatter.formatGetter(optionalClauseExpression);
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

        private final ArrayList<InstantiatedParameterData>  instantiatedParameters;
        private final boolean                               needsChildrenInitialization;
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

        private final String    getter;
        private final String    indirectGetter;
        private final String    setter;
        private final String    indirectSetter;
        private final String    typeName;
        private final boolean   containsIndex;
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

        private final String    bitFieldLength;
        private final boolean   hasFullRange;
        private final boolean   checkLowerBound;
        private final String    lowerBound;
        private final String    upperBound;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayType baseType, CompoundType parentType,
                CppNativeTypeMapper cppNativeTypeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, boolean withWriterCode)
                        throws ZserioEmitException
        {
            final ZserioType elementType = TypeReference.resolveBaseType(baseType.getElementType());

            isImplicit = baseType.isImplicit();
            length = createLength(baseType, cppExpressionFormatter);
            indirectLength = createLength(baseType, cppIndirectExpressionFormatter);
            elementZserioTypeName = ZserioTypeUtil.getFullName(elementType);
            elementCppTypeName = nativeType.getElementType().getFullName();
            requiresElementBitSize = nativeType.requiresElementBitSize();
            requiresElementFactory = nativeType.requiresElementFactory();
            elementBitSizeValue = createBitSizeValue(elementType, cppExpressionFormatter);
            elementCompound = createCompound(cppNativeTypeMapper, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, parentType, elementType, withWriterCode);
            elementIntegerRange = createIntegerRange(cppNativeTypeMapper, elementType, cppExpressionFormatter);
        }

        public boolean getIsImplicit()
        {
            return isImplicit;
        }

        public String getLength()
        {
            return length;
        }

        public String getIndirectLength()
        {
            return indirectLength;
        }

        public String getElementZserioTypeName()
        {
            return elementZserioTypeName;
        }

        public String getElementCppTypeName()
        {
            return elementCppTypeName;
        }

        public boolean getRequiresElementBitSize()
        {
            return requiresElementBitSize;
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

        private final boolean       isImplicit;
        private final String        length;
        private final String        indirectLength;
        private final String        elementZserioTypeName;
        private final String        elementCppTypeName;
        private final boolean       requiresElementBitSize;
        private final boolean       requiresElementFactory;
        private final String        elementBitSizeValue;
        private final Compound      elementCompound;
        private final IntegerRange  elementIntegerRange;
    }

    public static class OptionalHolder
    {
        public OptionalHolder(NativeOptionalHolderType nativeOptionalHolderType)
        {
            cppTypeName = nativeOptionalHolderType.getFullName();
            cppArgumentTypeName = nativeOptionalHolderType.getArgumentTypeName();
        }

        public String getCppTypeName()
        {
            return cppTypeName;
        }

        public String getCppArgumentTypeName()
        {
            return cppArgumentTypeName;
        }

        private final String cppTypeName;
        private final String cppArgumentTypeName;
    }

    private static Optional createOptional(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        if (!field.getIsOptional())
            return null;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, indicatorName, cppExpressionFormatter);
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

    private static String createConstraint(Field field, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return cppExpressionFormatter.formatGetter(constraintExpression);
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

    private static OptionalHolder createOptionalHolder(ZserioType fieldType, ZserioType baseFieldType,
            CompoundType parentType, boolean isOptionalField, CppNativeTypeMapper cppNativeTypeMapper,
            IncludeCollector includeCollector) throws ZserioEmitException
    {
        ZserioType fieldInstantiatedType = baseFieldType;
        if (baseFieldType instanceof TypeInstantiation)
            fieldInstantiatedType = ((TypeInstantiation)baseFieldType).getBaseType();

        final boolean isCompoundField = (fieldInstantiatedType instanceof CompoundType);
        if (!isOptionalField && !isCompoundField)
            return null;

        final boolean containsRecursion = (fieldInstantiatedType == parentType);
        final boolean useHeapOptionalHolder = (isCompoundField) ? containsRecursion : false;
        final NativeOptionalHolderType nativeOptionalHolderType =
                cppNativeTypeMapper.getCppOptionalHolderType(fieldType, isOptionalField, useHeapOptionalHolder);
        includeCollector.addHeaderIncludesForType(nativeOptionalHolderType);

        return new OptionalHolder(nativeOptionalHolderType);
    }

    private final Optional                      optional;
    private final Compound                      compound;
    private final String                        name;
    private final String                        cppTypeName;
    private final String                        cppArgumentTypeName;
    private final String                        zserioTypeName;
    private final String                        getterName;
    private final String                        setterName;
    private final IntegerRange                  integerRange;
    private final String                        alignmentValue;
    private final String                        initializer;
    private final boolean                       usesAnyHolder;
    private final boolean                       isSimpleType;
    private final boolean                       isEnum;
    private final String                        constraint;
    private final Offset                        offset;
    private final Array                         array;
    private final RuntimeFunctionTemplateData   runtimeFunction;
    private final String                        bitSizeValue;
    private final OptionalHolder                optionalHolder;
    private final boolean                       withWriterCode;
}
