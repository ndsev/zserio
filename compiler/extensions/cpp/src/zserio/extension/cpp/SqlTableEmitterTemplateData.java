package zserio.extension.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.IntegerType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.common.sql.SqlNativeTypeMapper;
import zserio.extension.common.sql.types.NativeBlobType;
import zserio.extension.common.sql.types.NativeIntegerType;
import zserio.extension.common.sql.types.NativeRealType;
import zserio.extension.common.sql.types.SqlNativeType;
import zserio.extension.cpp.SqlTableEmitterTemplateData.FieldTemplateData.SqlRangeCheckData;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeIntegralType;
import zserio.tools.HashUtil;

/**
 * FreeMarker template data for SqlTableEmitter.
 */
public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType)
            throws ZserioExtensionException
    {
        super(context, tableType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        sqlConstraint = createSqlConstraint(tableType.getSqlConstraint(), cppExpressionFormatter);
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        final List<Field> tableFields = tableType.getFields();
        fields = new ArrayList<FieldTemplateData>(tableFields.size());
        final ExpressionFormatter cppSqlIndirectExpressionFormatter =
                context.getIndirectExpressionFormatter(this, "row");
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        explicitParameters = new TreeSet<ExplicitParameterTemplateData>();
        boolean hasImplicitParameters = false;
        boolean requiresOwnerContext = false;
        for (Field tableField : tableFields)
        {
            final FieldTemplateData field = new FieldTemplateData(cppNativeMapper, cppExpressionFormatter,
                    cppSqlIndirectExpressionFormatter, sqlNativeTypeMapper, tableType, tableField, this);
            fields.add(field);

            if (field.getHasImplicitParameters())
                hasImplicitParameters = true;

            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : field.getTypeParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    final String expression = parameterTemplateData.getExpression();
                    final NativeTypeInfoTemplateData typeInfo = parameterTemplateData.getTypeInfo();
                    explicitParameters.add(new ExplicitParameterTemplateData(expression, typeInfo));
                }
                else
                {
                    if (parameterTemplateData.getRequiresOwnerContext())
                        requiresOwnerContext = true;
                }
            }
        }
        this.hasImplicitParameters = hasImplicitParameters;
        this.requiresOwnerContext = requiresOwnerContext;
        this.needsChildrenInitialization = tableType.needsChildrenInitialization();

        templateInstantiation = TemplateInstantiationTemplateData.create(context, tableType, this);
    }

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<ExplicitParameterTemplateData> getExplicitParameters()
    {
        return explicitParameters;
    }

    public boolean getHasImplicitParameters()
    {
        return hasImplicitParameters;
    }

    public boolean getRequiresOwnerContext()
    {
        return requiresOwnerContext;
    }

    public boolean getNeedsChildrenInitialization()
    {
        return needsChildrenInitialization;
    }

    public String getSqlConstraint()
    {
        return sqlConstraint;
    }

    public String getVirtualTableUsing()
    {
        return virtualTableUsing;
    }

    public boolean getNeedsTypesInSchema()
    {
        return needsTypesInSchema;
    }

    public boolean getIsWithoutRowId()
    {
        return isWithoutRowId;
    }

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(String expression, NativeTypeInfoTemplateData typeInfo)
        {
            this.expression = expression;
            this.typeInfo = typeInfo;
        }

        public String getExpression()
        {
            return expression;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            int result = expression.compareTo(other.expression);
            if (result != 0)
                return result;

            return typeInfo.getTypeFullName().compareTo(other.typeInfo.getTypeFullName());
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof ExplicitParameterTemplateData)
            {
                return compareTo((ExplicitParameterTemplateData)other) == 0;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, expression);
            hash = HashUtil.hash(hash, typeInfo.getTypeFullName());
            return hash;
        }

        private final String expression;
        private final NativeTypeInfoTemplateData typeInfo;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType table, Field field, IncludeCollector includeCollector)
                        throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();

            typeInfo = new NativeTypeInfoTemplateData(nativeFieldType, fieldTypeInstantiation);

            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            sqlConstraint = createSqlConstraint(fieldSqlConstraint, cppExpressionFormatter);
            isNotNull = !SqlConstraint.isNullAllowed(fieldSqlConstraint);
            isPrimaryKey = table.isFieldPrimaryKey(field);
            isVirtual = field.isVirtual();

            getterName = AccessorNameFormatter.getGetterName(field);
            setterName = AccessorNameFormatter.getSetterName(field);
            resetterName = AccessorNameFormatter.getResetterName(field);
            isSetIndicatorName = AccessorNameFormatter.getIsSetIndicatorName(field);

            typeParameters = new ArrayList<ParameterTemplateData>();
            boolean hasImplicitParameters = false;
            boolean hasExplicitParameters = false;
            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter parameter : parameterizedInstantiation.getInstantiatedParameters())
                {
                    final ParameterTemplateData parameterTemplateData = new ParameterTemplateData(
                            cppNativeMapper, cppSqlIndirectExpressionFormatter,
                            table, field, parameter, includeCollector);
                    typeParameters.add(parameterTemplateData);
                    if (parameterTemplateData.getIsExplicit())
                        hasExplicitParameters = true;
                    else
                        hasImplicitParameters = true;
                }
            }
            this.hasImplicitParameters = hasImplicitParameters;
            this.hasExplicitParameters = hasExplicitParameters;

            bitSize = BitSizeTemplateData.create(fieldTypeInstantiation, cppSqlIndirectExpressionFormatter);
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            underlyingTypeInfo = createUnderlyingTypeInfo(cppNativeMapper, fieldBaseType, includeCollector);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
            sqlRangeCheckData = createRangeCheckData(cppNativeMapper, fieldBaseType,
                    cppSqlIndirectExpressionFormatter, fieldTypeInstantiation);
            needsChildrenInitialization = (fieldBaseType instanceof CompoundType) &&
                    ((CompoundType)fieldBaseType).needsChildrenInitialization();
        }

        public String getName()
        {
            return name;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getSqlConstraint()
        {
            return sqlConstraint;
        }

        public boolean getIsNotNull()
        {
            return isNotNull;
        }

        public boolean getIsPrimaryKey()
        {
            return isPrimaryKey;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public String getGetterName()
        {
            return getterName;
        }

        public String getSetterName()
        {
            return setterName;
        }

        public String getResetterName()
        {
            return resetterName;
        }

        public String getIsSetIndicatorName()
        {
            return isSetIndicatorName;
        }

        public Iterable<ParameterTemplateData> getTypeParameters()
        {
            return typeParameters;
        }

        public boolean getHasImplicitParameters()
        {
            return hasImplicitParameters;
        }

        public boolean getHasExplicitParameters()
        {
            return hasExplicitParameters;
        }

        public BitSizeTemplateData getBitSize()
        {
            return bitSize;
        }

        public NativeTypeInfoTemplateData getUnderlyingTypeInfo()
        {
            return underlyingTypeInfo;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public SqlRangeCheckData getSqlRangeCheckData()
        {
            return sqlRangeCheckData;
        }

        public boolean getNeedsChildrenInitialization()
        {
            return needsChildrenInitialization;
        }

        public static class ParameterTemplateData
        {
            public ParameterTemplateData(CppNativeMapper cppNativeMapper,
                    ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlTableType tableType, Field field,
                    InstantiatedParameter instantiatedParameter,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final Parameter parameter = instantiatedParameter.getParameter();
                final TypeReference parameterTypeReference = parameter.getTypeReference();
                final CppNativeType parameterNativeType = cppNativeMapper.getCppType(parameterTypeReference);
                includeCollector.addHeaderIncludesForType(parameterNativeType);

                name = parameter.getName();
                typeInfo = new NativeTypeInfoTemplateData(parameterNativeType, parameterTypeReference);
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
                requiresOwnerContext = argumentExpression.requiresOwnerContext();
                getterName = AccessorNameFormatter.getGetterName(parameter);
            }

            public String getName()
            {
                return name;
            }

            public NativeTypeInfoTemplateData getTypeInfo()
            {
                return typeInfo;
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            public boolean getRequiresOwnerContext()
            {
                return requiresOwnerContext;
            }

            public String getGetterName()
            {
                return getterName;
            }

            private final String name;
            private final NativeTypeInfoTemplateData typeInfo;
            private final boolean isExplicit;
            private final String expression;
            private final boolean requiresOwnerContext;
            private final String getterName;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioExtensionException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(
                        field.getTypeInstantiation());
                name = sqlNativeType.getFullName();
                isBlob = sqlNativeType instanceof NativeBlobType;
                isInteger = sqlNativeType instanceof NativeIntegerType;
                isReal = sqlNativeType instanceof NativeRealType;
            }

            public String getName()
            {
                return name;
            }

            public boolean getIsBlob()
            {
                return isBlob;
            }

            public boolean getIsInteger()
            {
                return isInteger;
            }

            public boolean getIsReal()
            {
                return isReal;
            }

            private final String name;
            private final boolean isBlob;
            private final boolean isInteger;
            private final boolean isReal;
        }

        public static class SqlRangeCheckData
        {
            public SqlRangeCheckData(boolean checkLowerBound, String lowerBound, String upperBound,
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

        private static NativeTypeInfoTemplateData createUnderlyingTypeInfo(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            TypeInstantiation baseTypeInstantiation;
            if (fieldBaseType instanceof EnumType)
            {
                baseTypeInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
            }
            else if (fieldBaseType instanceof BitmaskType)
            {
                baseTypeInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();
            }
            else
            {
                return null;
            }

            final CppNativeType nativeBaseType = cppNativeMapper.getCppType(baseTypeInstantiation);
            includeCollector.addCppIncludesForType(nativeBaseType);

            return new NativeTypeInfoTemplateData(nativeBaseType, baseTypeInstantiation);
        }

        private final String name;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String sqlConstraint;
        private final boolean isNotNull;
        private final boolean isPrimaryKey;
        private final boolean isVirtual;
        private final String getterName;
        private final String setterName;
        private final String resetterName;
        private final String isSetIndicatorName;
        private final List<ParameterTemplateData> typeParameters;
        private final boolean hasImplicitParameters;
        private final boolean hasExplicitParameters;
        private final BitSizeTemplateData bitSize;
        private final NativeTypeInfoTemplateData underlyingTypeInfo;
        private final SqlTypeTemplateData sqlTypeData;
        private final SqlRangeCheckData sqlRangeCheckData;
        private final boolean needsChildrenInitialization;
    }

    private static String createSqlConstraint(SqlConstraint sqlConstraint,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        if (sqlConstraint == null)
            return null;

        return cppExpressionFormatter.formatGetter(sqlConstraint.getConstraintExpr());
    }

    private static SqlRangeCheckData createRangeCheckData(CppNativeMapper cppNativeMapper,
            ZserioType fieldBaseType, ExpressionFormatter cppSqlIndirectExpressionFormatter,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        TypeInstantiation baseTypeInstantiation = typeInstantiation;
        if (fieldBaseType instanceof EnumType)
        {
            baseTypeInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
        }
        else if (fieldBaseType instanceof BitmaskType)
        {
            baseTypeInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();
        }

        // in SQL, don't do range check non-integer types
        if (!(baseTypeInstantiation.getBaseType() instanceof IntegerType))
            return null;

        final String bitFieldLength = getDynamicBitFieldLength(baseTypeInstantiation,
                cppSqlIndirectExpressionFormatter);
        final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(baseTypeInstantiation);
        final boolean isSigned = nativeType.isSigned();
        final IntegerType typeToCheck = (IntegerType)baseTypeInstantiation.getBaseType();

        final BigInteger zserioLowerBound = typeToCheck.getLowerBound(baseTypeInstantiation);
        final BigInteger zserioUpperBound = typeToCheck.getUpperBound(baseTypeInstantiation);
        boolean checkLowerBound = true;
        boolean checkUpperBound = true;
        // since we use sqlite_column_int64, it has no sense to test 64-bit types
        final NativeIntegralType sqlNativeType = (isSigned) ? cppNativeMapper.getInt64Type() :
                cppNativeMapper.getUInt64Type();
        if (bitFieldLength == null)
        {
            final BigInteger nativeLowerBound = sqlNativeType.getLowerBound();
            checkLowerBound = nativeLowerBound.compareTo(zserioLowerBound) < 0;

            final BigInteger nativeUpperBound = sqlNativeType.getUpperBound();
            checkUpperBound = nativeUpperBound.compareTo(zserioUpperBound) > 0;
            final boolean hasFullRange = !checkLowerBound && !checkUpperBound;
            if (hasFullRange)
                return null;
        }

        final String lowerBound = nativeType.formatLiteral(zserioLowerBound);
        final String upperBound = nativeType.formatLiteral(zserioUpperBound);
        final NativeIntegralTypeInfoTemplateData typeInfo =
                new NativeIntegralTypeInfoTemplateData(sqlNativeType, baseTypeInstantiation);

        return new SqlRangeCheckData(checkLowerBound, lowerBound, upperBound, typeInfo, bitFieldLength);
    }

    private static String getDynamicBitFieldLength(TypeInstantiation instantiation,
            ExpressionFormatter cppSqlIndirectExpressionFormatter) throws ZserioExtensionException
    {
        if (!(instantiation instanceof DynamicBitFieldInstantiation))
            return null;

        final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                (DynamicBitFieldInstantiation)instantiation;
        return cppSqlIndirectExpressionFormatter.formatGetter(
                dynamicBitFieldInstantiation.getLengthExpression());
    }

    private final List<FieldTemplateData> fields;
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters;
    private final boolean hasImplicitParameters;
    private final boolean requiresOwnerContext;
    private final boolean needsChildrenInitialization;
    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;
    private final TemplateInstantiationTemplateData templateInstantiation;
}
