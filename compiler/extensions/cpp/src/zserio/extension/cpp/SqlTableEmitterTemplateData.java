package zserio.extension.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.IntegerType;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
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

public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType)
            throws ZserioExtensionException
    {
        super(context, tableType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        sqlConstraint = createSqlConstraint(tableType.getSqlConstraint());
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
            final FieldTemplateData field = new FieldTemplateData(cppNativeMapper,
                    cppSqlIndirectExpressionFormatter, sqlNativeTypeMapper, tableType, tableField, this);
            fields.add(field);

            if (field.getHasImplicitParameters())
                hasImplicitParameters = true;

            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : field.getTypeParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    final String expression = parameterTemplateData.getExpression();
                    final String cppTypeName = parameterTemplateData.getCppTypeName();
                    final boolean isSimpleType = parameterTemplateData.getIsSimpleType();
                    explicitParameters.add(new ExplicitParameterTemplateData(expression, cppTypeName,
                            isSimpleType));
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

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(String expression, String cppTypeName, boolean isSimpleType)
        {
            this.expression = expression;
            this.cppTypeName = cppTypeName;
            this.isSimpleType = isSimpleType;
        }

        public String getExpression()
        {
            return expression;
        }

        public String getCppTypeName()
        {
            return cppTypeName;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            int result = expression.compareTo(other.expression);
            if (result != 0)
                return result;

            return cppTypeName.compareTo(other.cppTypeName);
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
            hash = HashUtil.hash(hash, cppTypeName);
            return hash;
        }

        private final String expression;
        private final String cppTypeName;
        private final boolean isSimpleType;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType table, Field field, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            cppTypeName = nativeFieldType.getFullName();
            cppArgumentTypeName = nativeFieldType.getArgumentTypeName();

            typeInfo = new TypeInfoTemplateData(fieldTypeInstantiation.getTypeReference(), nativeFieldType);

            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            sqlConstraint = createSqlConstraint(fieldSqlConstraint);
            isNotNull = !SqlConstraint.isNullAllowed(fieldSqlConstraint);
            isPrimaryKey = table.isFieldPrimaryKey(field);
            isVirtual = field.isVirtual();

            getterName = AccessorNameFormatter.getGetterName(field);
            setterName = AccessorNameFormatter.getSetterName(field);
            resetterName = AccessorNameFormatter.getResetterName(field);
            indicatorName = AccessorNameFormatter.getIndicatorName(field);

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

            bitSize = createBitSize(fieldTypeInstantiation, cppSqlIndirectExpressionFormatter);
            isSimpleType = nativeFieldType.isSimpleType();
            isBoolean = fieldBaseType instanceof BooleanType;
            enumData = createEnumTemplateData(cppNativeMapper, fieldBaseType, includeCollector);
            bitmaskData = createBitmaskTemplateData(cppNativeMapper, fieldBaseType, includeCollector);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);

            TypeInstantiation rangeCheckInstantiation = fieldTypeInstantiation;
            if (enumData != null)
                rangeCheckInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
            else if (bitmaskData != null)
                rangeCheckInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();
            sqlRangeCheckData = createRangeCheckData(cppNativeMapper, cppSqlIndirectExpressionFormatter,
                    rangeCheckInstantiation);
            needsChildrenInitialization = (fieldBaseType instanceof CompoundType) &&
                    ((CompoundType)fieldBaseType).needsChildrenInitialization();
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

        public TypeInfoTemplateData getTypeInfo()
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

        public String getIndicatorName()
        {
            return indicatorName;
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

        public String getBitSize()
        {
            return bitSize;
        }

        public boolean getIsSimpleType()
        {
            return isSimpleType;
        }

        public boolean getIsBoolean()
        {
            return isBoolean;
        }

        public EnumTemplateData getEnumData()
        {
            return enumData;
        }

        public BitmaskTemplateData getBitmaskData()
        {
            return bitmaskData;
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
                final CppNativeType parameterNativeType =
                        cppNativeMapper.getCppType(parameter.getTypeReference());
                includeCollector.addHeaderIncludesForType(parameterNativeType);

                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                definitionName = parameter.getName();
                cppTypeName = parameterNativeType.getFullName();
                isSimpleType = parameterNativeType.isSimpleType();
                expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
                requiresOwnerContext = argumentExpression.requiresOwnerContext();
                getterName = AccessorNameFormatter.getGetterName(parameter);
            }

            public String getDefinitionName()
            {
                return definitionName;
            }

            public String getCppTypeName()
            {
                return cppTypeName;
            }

            public boolean getIsSimpleType()
            {
                return isSimpleType;
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

            private final String definitionName;
            private final String cppTypeName;
            private final boolean isSimpleType;
            private final boolean isExplicit;
            private final String expression;
            private final boolean requiresOwnerContext;
            private final String getterName;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(CppNativeMapper cppNativeMapper, EnumType enumType,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final CppNativeType nativeBaseType =
                        cppNativeMapper.getCppType(enumType.getTypeInstantiation());
                includeCollector.addCppIncludesForType(nativeBaseType);
                baseCppTypeName = nativeBaseType.getFullName();
            }

            public String getBaseCppTypeName()
            {
                return baseCppTypeName;
            }

            private final String baseCppTypeName;
        }

        public static class BitmaskTemplateData
        {
            public BitmaskTemplateData(CppNativeMapper cppNativeMapper, BitmaskType bitmaskType,
                    IncludeCollector includeCollector) throws ZserioExtensionException
            {
                final CppNativeType nativeBaseType =
                        cppNativeMapper.getCppType(bitmaskType.getTypeInstantiation());
                includeCollector.addCppIncludesForType(nativeBaseType);
                baseCppTypeName = nativeBaseType.getFullName();
            }

            public String getBaseCppTypeName()
            {
                return baseCppTypeName;
            }

            private final String baseCppTypeName;
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
                    String sqlCppTypeName, String bitFieldLength, boolean isSigned) throws ZserioExtensionException
            {
                this.checkLowerBound = checkLowerBound;
                this.lowerBound = lowerBound;
                this.upperBound = upperBound;
                this.sqlCppTypeName = sqlCppTypeName;
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

            public String getSqlCppTypeName()
            {
                return sqlCppTypeName;
            }

            public String getBitFieldLength()
            {
                return bitFieldLength;
            }

            public boolean getIsSigned()
            {
                return isSigned;
            }

            private final boolean checkLowerBound;
            private final String lowerBound;
            private final String upperBound;
            private final String sqlCppTypeName;
            private final String bitFieldLength;
            private final boolean isSigned;
        }

        private static String createBitSize(TypeInstantiation typeInstantiation,
                ExpressionFormatter cppSqlIndirectExpressionFormatter) throws ZserioExtensionException
        {
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                return CppLiteralFormatter.formatUInt8Literal(
                        ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            }
            else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                return cppSqlIndirectExpressionFormatter.formatGetter(
                        ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());
            }

            return null;
        }

        private static EnumTemplateData createEnumTemplateData(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            if (!(fieldBaseType instanceof EnumType))
                return null;

            return new EnumTemplateData(cppNativeMapper, (EnumType)fieldBaseType, includeCollector);
        }

        private static BitmaskTemplateData createBitmaskTemplateData(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType, IncludeCollector includeCollector) throws ZserioExtensionException
        {
            if (!(fieldBaseType instanceof BitmaskType))
                return null;

            return new BitmaskTemplateData(cppNativeMapper, (BitmaskType)fieldBaseType, includeCollector);
        }

        private final String name;
        private final String cppTypeName;
        private final String cppArgumentTypeName;
        private final TypeInfoTemplateData typeInfo;
        private final String sqlConstraint;
        private final boolean isNotNull;
        private final boolean isPrimaryKey;
        private final boolean isVirtual;
        private final String getterName;
        private final String setterName;
        private final String resetterName;
        private final String indicatorName;
        private final List<ParameterTemplateData> typeParameters;
        private final boolean hasImplicitParameters;
        private final boolean hasExplicitParameters;
        private final String bitSize;
        private final boolean isSimpleType;
        private final boolean isBoolean;
        private final EnumTemplateData enumData;
        private final BitmaskTemplateData bitmaskData;
        private final SqlTypeTemplateData sqlTypeData;
        private final SqlRangeCheckData sqlRangeCheckData;
        private final boolean needsChildrenInitialization;
    }

    private static String createSqlConstraint(SqlConstraint sqlConstraint) throws ZserioExtensionException
    {
        if (sqlConstraint == null)
            return null;

        final String stringValue = sqlConstraint.getConstraintExpr().getStringValue();
        if (stringValue == null)
            throw new ZserioExtensionException("Unexpected sql constraint which is a non-constant string!");

        return CppLiteralFormatter.formatStringLiteral(stringValue);
    }

    private static SqlRangeCheckData createRangeCheckData(CppNativeMapper cppNativeMapper,
            ExpressionFormatter cppSqlIndirectExpressionFormatter, TypeInstantiation typeInstantiation)
                    throws ZserioExtensionException
    {
        // in SQL, don't do range check non-integer types
        if (!(typeInstantiation.getBaseType() instanceof IntegerType))
            return null;

        final String bitFieldLength = getDynamicBitFieldLength(typeInstantiation,
                cppSqlIndirectExpressionFormatter);
        final NativeIntegralType nativeType = cppNativeMapper.getCppIntegralType(typeInstantiation);
        final boolean isSigned = nativeType.isSigned();
        final IntegerType typeToCheck = (IntegerType)typeInstantiation.getBaseType();

        final BigInteger zserioLowerBound = typeToCheck.getLowerBound(typeInstantiation);
        final BigInteger zserioUpperBound = typeToCheck.getUpperBound(typeInstantiation);
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
        final String sqlCppTypeName = sqlNativeType.getFullName();

        return new SqlRangeCheckData(checkLowerBound, lowerBound, upperBound, sqlCppTypeName,
                bitFieldLength, isSigned);
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
}
