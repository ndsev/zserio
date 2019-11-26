package zserio.emit.cpp98;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.EnumType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.CppNativeType;
import zserio.emit.cpp98.types.NativeArrayType;
import zserio.emit.cpp98.types.NativeIntegralType;
import zserio.emit.cpp98.types.NativeOptionalHolderType;

public class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(CppNativeMapper cppNativeMapper,
            CompoundType parentType, Field field,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            IncludeCollector includeCollector, boolean withWriterCode) throws ZserioEmitException
    {
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final ZserioType fieldType = fieldTypeInstantiation.getType();
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();

        optional = createOptional(field, cppExpressionFormatter);
        compound = createCompound(cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                parentType, fieldTypeInstantiation, withWriterCode);

        final CppNativeType fieldNativeType = cppNativeMapper.getCppType(fieldTypeInstantiation);
        includeCollector.addHeaderIncludesForType(fieldNativeType);

        name = field.getName();
        cppTypeName = fieldNativeType.getFullName();
        cppArgumentTypeName = fieldNativeType.getArgumentTypeName();
        zserioTypeName = ZserioTypeUtil.getFullName(fieldType);

        getterName = AccessorNameFormatter.getGetterName(field);
        setterName = AccessorNameFormatter.getSetterName(field);

        integerRange = createIntegerRange(cppNativeMapper, fieldTypeInstantiation, cppExpressionFormatter);

        alignmentValue = createAlignmentValue(field, cppExpressionFormatter);
        initializer = createInitializer(field, cppExpressionFormatter);

        usesAnyHolder = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        isSimpleType = fieldNativeType.isSimpleType();
        isEnum = fieldBaseType instanceof EnumType;

        constraint = createConstraint(field, cppExpressionFormatter);

        offset = createOffset(field, cppNativeMapper, cppExpressionFormatter,
                cppIndirectExpressionFormatter);
        array = createArray(fieldNativeType, fieldTypeInstantiation, parentType, cppNativeMapper,
                cppExpressionFormatter, cppIndirectExpressionFormatter, withWriterCode);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(fieldTypeInstantiation,
                cppExpressionFormatter);
        bitSizeValue = createBitSizeValue(fieldTypeInstantiation, cppExpressionFormatter);
        final boolean isOptionalField = (optional != null);
        optionalHolder = createOptionalHolder(fieldTypeInstantiation, parentType, isOptionalField,
                cppNativeMapper, includeCollector);
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
        public Compound(CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, CompoundType owner,
                ParameterizedTypeInstantiation parameterizedTypeInstantiation, boolean withWriterCode)
                        throws ZserioEmitException
        {
            final CompoundType baseType = parameterizedTypeInstantiation.getBaseType();
            final List<InstantiatedParameter> parameters =
                    parameterizedTypeInstantiation.getInstantiatedParameters();
            instantiatedParameters = new ArrayList<InstantiatedParameterData>(parameters.size());
            for (InstantiatedParameter parameter : parameters)
            {
                instantiatedParameters.add(new InstantiatedParameterData(cppExpressionFormatter,
                        cppIndirectExpressionFormatter, parameter));
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

        private final String    getter;
        private final String    indirectGetter;
        private final String    setter;
        private final String    indirectSetter;
        private final String    typeName;
        private final boolean   containsIndex;
    }

    public static class IntegerRange
    {
        public IntegerRange(CppNativeMapper cppNativeMapper, TypeInstantiation typeInstantiation,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            IntegerType typeToCheck = (IntegerType)typeInstantiation.getBaseType();
            bitFieldLength = getDynamicBitFieldLength(typeInstantiation, cppExpressionFormatter);
            isSigned = typeToCheck.isSigned();

            final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(typeInstantiation);
            final BigInteger zserioLowerBound = typeToCheck.getLowerBound(typeInstantiation);
            final BigInteger nativeLowerBound = nativeType.getLowerBound();
            checkLowerBound = (bitFieldLength != null && isSigned) ||
                    nativeLowerBound.compareTo(zserioLowerBound) < 0;

            final BigInteger zserioUpperBound = typeToCheck.getUpperBound(typeInstantiation);
            final BigInteger nativeUpperBound = nativeType.getUpperBound();
            final boolean checkUpperBound = nativeUpperBound.compareTo(zserioUpperBound) > 0;

            // Zserio types that have the same bounds as their native type are not checked
            hasFullRange = (bitFieldLength == null) && !checkLowerBound && !checkUpperBound;

            lowerBound = nativeType.formatLiteral(zserioLowerBound);
            upperBound = nativeType.formatLiteral(zserioUpperBound);
        }

        public String getBitFieldLength()
        {
            return bitFieldLength;
        }

        public boolean getIsSigned()
        {
            return isSigned;
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

        private static String getDynamicBitFieldLength(TypeInstantiation instantiation,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            if (!(instantiation instanceof DynamicBitFieldInstantiation))
                return null;

            final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                    (DynamicBitFieldInstantiation)instantiation;
            return cppExpressionFormatter.formatGetter(dynamicBitFieldInstantiation.getLengthExpression());
        }

        private final String    bitFieldLength;
        private final boolean   isSigned;
        private final boolean   hasFullRange;
        private final boolean   checkLowerBound;
        private final String    lowerBound;
        private final String    upperBound;
    }

    public static class Array
    {
        public Array(NativeArrayType nativeType, ArrayInstantiation arrayInstantiation, CompoundType parentType,
                CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppIndirectExpressionFormatter, boolean withWriterCode)
                        throws ZserioEmitException
        {
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            final ZserioType elementBaseType = elementTypeInstantiation.getBaseType();

            isImplicit = arrayInstantiation.isImplicit();
            length = createLength(arrayInstantiation, cppExpressionFormatter);
            indirectLength = createLength(arrayInstantiation, cppIndirectExpressionFormatter);
            elementZserioTypeName = ZserioTypeUtil.getFullName(elementBaseType);
            elementCppTypeName = nativeType.getElementType().getFullName();
            requiresElementBitSize = nativeType.requiresElementBitSize();
            requiresElementFactory = nativeType.requiresElementFactory();
            elementBitSizeValue = createBitSizeValue(elementTypeInstantiation, cppExpressionFormatter);
            elementCompound = createCompound(cppNativeMapper, cppExpressionFormatter,
                    cppIndirectExpressionFormatter, parentType, elementTypeInstantiation, withWriterCode);
            elementIntegerRange = createIntegerRange(cppNativeMapper, elementTypeInstantiation,
                    cppExpressionFormatter);
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

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
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
        if (!field.isOptional())
            return null;

        final Expression optionalClauseExpression = field.getOptionalClauseExpr();
        final String indicatorName = AccessorNameFormatter.getIndicatorName(field);

        return new Optional(optionalClauseExpression, indicatorName, cppExpressionFormatter);
    }

    private static IntegerRange createIntegerRange(CppNativeMapper cppNativeMapper,
            TypeInstantiation typeInstantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        if (!(typeInstantiation.getBaseType() instanceof IntegerType))
            return null;

        return new IntegerRange(cppNativeMapper, typeInstantiation, cppExpressionFormatter);
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

    private static Array createArray(CppNativeType cppNativeType, TypeInstantiation instantiation,
            CompoundType parentType, CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            boolean withWriterCode) throws ZserioEmitException
    {
        if (!(instantiation instanceof ArrayInstantiation))
            return null;

        if (cppNativeType instanceof NativeOptionalHolderType)
            cppNativeType = ((NativeOptionalHolderType)cppNativeType).getWrappedType();

        if (!(cppNativeType instanceof NativeArrayType))
            throw new ZserioEmitException("Inconsistent instantiation '" + instantiation.getClass().getName() +
                    "' and native type '" + cppNativeType.getClass().getName() + "'!");

        return new Array((NativeArrayType)cppNativeType, (ArrayInstantiation)instantiation, parentType,
                cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter, withWriterCode);
    }

    static String createBitSizeValue(TypeInstantiation instantiation, ExpressionFormatter cppExpressionFormatter)
            throws ZserioEmitException
    {
        String value;
        if (instantiation.getBaseType() instanceof FixedSizeType)
        {
            value = CppLiteralFormatter.formatUInt8Literal(
                    ((FixedSizeType)instantiation.getBaseType()).getBitSize());
        }
        else if (instantiation instanceof DynamicBitFieldInstantiation)
        {
            final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                    (DynamicBitFieldInstantiation)instantiation;
            value = cppExpressionFormatter.formatGetter(dynamicBitFieldInstantiation.getLengthExpression());
        }
        else
        {
            value = null;
        }

        return value;
    }

    private static Compound createCompound(CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppIndirectExpressionFormatter,
            CompoundType owner, TypeInstantiation typeInstantiation, boolean withWriterCode)
                    throws ZserioEmitException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            return new Compound(cppNativeMapper, cppExpressionFormatter, cppIndirectExpressionFormatter,
                    owner, (ParameterizedTypeInstantiation)typeInstantiation, withWriterCode);
        }
        else
        if (typeInstantiation.getBaseType() instanceof CompoundType)
        {
            return new Compound(typeInstantiation);
        }
        else
        {
            return null;
        }
    }

    private static OptionalHolder createOptionalHolder(TypeInstantiation fieldTypeInstantiation,
            CompoundType parentType, boolean isOptionalField, CppNativeMapper cppNativeMapper,
            IncludeCollector includeCollector) throws ZserioEmitException
    {
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
        final boolean isCompoundField = (fieldBaseType instanceof CompoundType);
        if (!isOptionalField && !isCompoundField)
            return null;

        final boolean containsRecursion = (fieldBaseType == parentType);
        final boolean useHeapOptionalHolder = (isCompoundField) ? containsRecursion : false;
        final NativeOptionalHolderType nativeOptionalHolderType = cppNativeMapper.getCppOptionalHolderType(
                fieldTypeInstantiation, isOptionalField, useHeapOptionalHolder);
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
