package zserio.extension.python;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.DocComment;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.IntegerType;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for compound fields, used from various template data.
 */
public final class CompoundFieldTemplateData
{
    public CompoundFieldTemplateData(TemplateDataContext context, CompoundType parentType, Field field,
            ImportCollector importCollector) throws ZserioExtensionException
    {
        name = field.getName();
        snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);

        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeType = pythonNativeMapper.getPythonType(fieldTypeInstantiation);
        importCollector.importType(nativeType);

        typeInfo = new NativeTypeInfoTemplateData(nativeType, fieldTypeInstantiation);
        propertyName = AccessorNameFormatter.getPropertyName(field);

        isPackable = field.isPackable();

        final ExpressionFormatter pythonExpressionFormatter =
                context.getPythonExpressionFormatter(importCollector);
        final boolean withRangeCheckCode = context.getWithRangeCheckCode();
        rangeCheck = createRangeCheck(fieldTypeInstantiation, withRangeCheckCode, pythonExpressionFormatter);
        final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
        optional = createOptional(field, fieldBaseType, parentType, pythonExpressionFormatter);

        alignmentValue = createAlignmentValue(field, pythonExpressionFormatter);
        initializer = createInitializer(field, pythonExpressionFormatter);
        constraint = createConstraint(field, pythonExpressionFormatter);

        usesChoiceMember = (parentType instanceof ChoiceType) || (parentType instanceof UnionType);

        bitSize = new BitSize(fieldTypeInstantiation, pythonExpressionFormatter);
        offset = createOffset(field, pythonExpressionFormatter);
        array = createArray(context, fieldTypeInstantiation, parentType, importCollector);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(fieldTypeInstantiation,
                pythonExpressionFormatter);
        compound = createCompound(context, fieldTypeInstantiation, importCollector);
        final List<DocComment> fieldDocComments = field.getDocComments();
        docComments = fieldDocComments.isEmpty() ?
                null : new DocCommentsTemplateData(context, fieldDocComments);
    }

    public String getName()
    {
        return name;
    }

    public String getSnakeCaseName()
    {
        return snakeCaseName;
    }

    public NativeTypeInfoTemplateData getTypeInfo()
    {
        return typeInfo;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public boolean getIsPackable()
    {
        return isPackable;
    }

    public RangeCheck getRangeCheck()
    {
        return rangeCheck;
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

    public String getConstraint()
    {
        return constraint;
    }

    public boolean getUsesChoiceMember()
    {
        return usesChoiceMember;
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

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public static class RangeCheck
    {
        public RangeCheck(BitFieldWithExpression bitFieldWithExpression, String lowerBound, String upperBound)
        {
            this.bitFieldWithExpression = bitFieldWithExpression;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public BitFieldWithExpression getBitFieldWithExpression()
        {
            return bitFieldWithExpression;
        }

        public String getLowerBound()
        {
            return lowerBound;
        }

        public String getUpperBound()
        {
            return upperBound;
        }

        private final BitFieldWithExpression bitFieldWithExpression;
        private final String lowerBound;
        private final String upperBound;
    }

    public static class BitFieldWithExpression
    {
        public BitFieldWithExpression(DynamicBitFieldInstantiation dynamicBitFieldInstantiation,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            lengthExpression = pythonExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());
            isSigned = dynamicBitFieldInstantiation.getBaseType().isSigned();
        }

        public String getLengthExpression()
        {
            return lengthExpression;
        }

        public boolean getIsSigned()
        {
            return isSigned;
        }

        private final String lengthExpression;
        private final boolean isSigned;
    }

    public static class Optional
    {
        public Optional(Field field, ExpressionFormatter pythonExpressionFormatter, boolean isRecursive)
                throws ZserioExtensionException
        {
            final Expression optionalClauseExpression = field.getOptionalClauseExpr();
            clause = (optionalClauseExpression == null) ? null :
                pythonExpressionFormatter.formatGetter(optionalClauseExpression);

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

    public static class BitSize
    {
        public BitSize(TypeInstantiation typeInstantiation, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioExtensionException
        {
            value = createValue(typeInstantiation, pythonExpressionFormatter);
            runtimeFunction = (value != null) ? null :
                PythonRuntimeFunctionDataCreator.createData(typeInstantiation, pythonExpressionFormatter);
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
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            String bitSizeOfValue = null;
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                bitSizeOfValue = PythonLiteralFormatter.formatDecimalLiteral(
                        ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            }
            else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)typeInstantiation;
                bitSizeOfValue = pythonExpressionFormatter.formatGetter(
                        dynamicBitFieldInstantiation.getLengthExpression());
            }

            return bitSizeOfValue;
        }

        private final String value;
        private final RuntimeFunctionTemplateData runtimeFunction;
    }

    public static class Offset
    {
        public Offset(Expression offsetExpression, ExpressionFormatter pythonExpressionFormatter)
                throws ZserioExtensionException
        {
            getter = pythonExpressionFormatter.formatGetter(offsetExpression);
            setter = pythonExpressionFormatter.formatSetter(offsetExpression);
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

        public boolean getContainsIndex()
        {
            return containsIndex;
        }

        private final String getter;
        private final String setter;
        private final boolean containsIndex;
    }

    public static class Array
    {
        public Array(TemplateDataContext context, ArrayInstantiation arrayInstantiation, ZserioType parentType,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            isImplicit = arrayInstantiation.isImplicit();
            isPacked = arrayInstantiation.isPacked();
            final ExpressionFormatter pythonExpressionFormatter =
                    context.getPythonExpressionFormatter(importCollector);
            length = createLength(arrayInstantiation, pythonExpressionFormatter);

            final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
            final TypeInstantiation elementTypeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            final PythonNativeType elementNativeType =
                    pythonNativeMapper.getPythonType(elementTypeInstantiation);
            importCollector.importType(elementNativeType);

            elementTypeInfo = new NativeTypeInfoTemplateData(elementNativeType, elementTypeInstantiation);
            final ZserioType elementBaseType = elementTypeInstantiation.getBaseType();
            elementIsRecursive = (elementBaseType == parentType);
            elementBitSize = new BitSize(elementTypeInstantiation, pythonExpressionFormatter);
            elementCompound = createCompound(context, elementTypeInstantiation, importCollector);
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

        public NativeTypeInfoTemplateData getElementTypeInfo()
        {
            return elementTypeInfo;
        }

        public boolean getElementIsRecursive()
        {
            return elementIsRecursive;
        }

        public BitSize getElementBitSize()
        {
            return elementBitSize;
        }

        public Compound getElementCompound()
        {
            return elementCompound;
        }

        private static String createLength(ArrayInstantiation arrayInstantiation,
                ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
        {
            final Expression lengthExpression = arrayInstantiation.getLengthExpression();
            if (lengthExpression == null)
                return null;

            return pythonExpressionFormatter.formatGetter(lengthExpression);
        }

        private final boolean isImplicit;
        private final boolean isPacked;
        private final String length;
        private final NativeTypeInfoTemplateData elementTypeInfo;
        private final boolean elementIsRecursive;
        private final BitSize elementBitSize;
        private final Compound elementCompound;
    }

    public static class Compound
    {
        public Compound(TemplateDataContext context,
                ParameterizedTypeInstantiation parameterizedTypeInstantiation, ImportCollector importCollector)
                        throws ZserioExtensionException
        {
            this(context, parameterizedTypeInstantiation.getBaseType(), importCollector);

            final ExpressionFormatter pythonExpressionFormatter =
                    context.getPythonExpressionFormatter(importCollector);
            for (InstantiatedParameter param : parameterizedTypeInstantiation.getInstantiatedParameters())
            {
                instantiatedParameters.add(new InstantiatedParameterData(pythonExpressionFormatter, param));
            }
        }

        public Compound(TemplateDataContext context, CompoundType compoundType, ImportCollector importCollector)
                throws ZserioExtensionException
        {
            instantiatedParameters = new ArrayList<InstantiatedParameterData>();
            parameters = new CompoundParameterTemplateData(context, compoundType, importCollector);
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
            public InstantiatedParameterData(ExpressionFormatter pythonExpressionFormatter,
                    InstantiatedParameter instantiatedParameter) throws ZserioExtensionException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                expression = pythonExpressionFormatter.formatGetter(argumentExpression);
                containsIndex = argumentExpression.containsIndex();
            }

            public String getExpression()
            {
                return expression;
            }

            public boolean getContainsIndex()
            {
                return containsIndex;
            }

            private final String expression;
            private final boolean containsIndex;
        }

        private final List<InstantiatedParameterData> instantiatedParameters;
        private final CompoundParameterTemplateData parameters;
    }

    private static RangeCheck createRangeCheck(TypeInstantiation typeInstantiation, boolean withRangeCheckCode,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        // don't do range check for non-integer type
        if (!withRangeCheckCode || !(baseType instanceof IntegerType))
            return null;

        final IntegerType integerType = (IntegerType)baseType;
        final BitFieldWithExpression bitFieldWithExpression = createBitFieldWithExpression(typeInstantiation,
                pythonExpressionFormatter);

        final BigInteger zserioLowerBound = integerType.getLowerBound(typeInstantiation);
        final String lowerBound = zserioLowerBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioLowerBound) : null;

        final BigInteger zserioUpperBound = integerType.getUpperBound(typeInstantiation);
        final String upperBound = zserioUpperBound != null ?
                PythonLiteralFormatter.formatDecimalLiteral(zserioUpperBound) : null;

        return new RangeCheck(bitFieldWithExpression, lowerBound, upperBound);
    }

    private static BitFieldWithExpression createBitFieldWithExpression(TypeInstantiation typeInstantiation,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof DynamicBitFieldInstantiation))
            return null;

        return new BitFieldWithExpression(
                (DynamicBitFieldInstantiation)typeInstantiation, pythonExpressionFormatter);
    }

    private static Optional createOptional(Field field, ZserioType fieldBaseType, CompoundType parentType,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        if (!field.isOptional())
            return null;

        final boolean isRecursive = (fieldBaseType == parentType);

        return new Optional(field, pythonExpressionFormatter, isRecursive);
    }

    private static String createAlignmentValue(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression alignmentExpression = field.getAlignmentExpr();
        if (alignmentExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(alignmentExpression);
    }

    private static String createInitializer(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression initializerExpression = field.getInitializerExpr();
        if (initializerExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(initializerExpression);
    }

    private static String createConstraint(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression constraintExpression = field.getConstraintExpr();
        if (constraintExpression == null)
            return null;

        return pythonExpressionFormatter.formatGetter(constraintExpression);
    }

    private static Offset createOffset(Field field, ExpressionFormatter pythonExpressionFormatter)
            throws ZserioExtensionException
    {
        final Expression offsetExpression = field.getOffsetExpr();
        if (offsetExpression == null)
            return null;

        return new Offset(offsetExpression, pythonExpressionFormatter);
    }

    private static Array createArray(TemplateDataContext context, TypeInstantiation typeInstantiation,
            ZserioType parentType, ImportCollector importCollector) throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof ArrayInstantiation))
            return null;

        return new Array(context, (ArrayInstantiation)typeInstantiation, parentType, importCollector);
    }

    private static Compound createCompound(TemplateDataContext context, TypeInstantiation typeInstantiation,
            ImportCollector importCollector) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            return new Compound(context, (ParameterizedTypeInstantiation)typeInstantiation, importCollector);
        else if (typeInstantiation.getBaseType() instanceof CompoundType)
            return new Compound(context, (CompoundType)typeInstantiation.getBaseType(), importCollector);
        else
            return null;
    }

    private final String name;
    private final String snakeCaseName;
    private final NativeTypeInfoTemplateData typeInfo;
    private final String propertyName;
    private final boolean isPackable;

    private final RangeCheck rangeCheck;
    private final Optional optional;

    private final String alignmentValue;
    private final String initializer;
    private final String constraint;

    private final boolean usesChoiceMember;

    private final BitSize bitSize;
    private final Offset offset;
    private final Array array;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final Compound compound;
    private final DocCommentsTemplateData docComments;
}
