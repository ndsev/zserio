package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ZserioType;
import zserio.ast.BitmaskType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.common.sql.SqlNativeTypeMapper;
import zserio.extension.common.sql.types.NativeBlobType;
import zserio.extension.common.sql.types.SqlNativeType;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeIntegralType;
import zserio.tools.HashUtil;

/**
 * FreeMarker template data for SqlTableEmitter.
 */
public final class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType, String tableRowName)
            throws ZserioExtensionException
    {
        super(context, tableType, tableType.getDocComments());

        rootPackageName = context.getJavaRootPackageName();
        this.withValidationCode = context.getWithValidationCode();

        rowName = tableRowName;
        final SqlConstraint tableSqlConstraint = tableType.getSqlConstraint();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        sqlConstraint = (tableSqlConstraint == null) ? null :
            javaExpressionFormatter.formatGetter(tableSqlConstraint.getConstraintExpr());
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        for (Field field: tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(javaNativeMapper, javaExpressionFormatter,
                    context.getJavaSqlExpressionFormatter(), context.getJavaSqlLambdaExpressionFormatter(),
                    sqlNativeTypeMapper, tableType, field);
            fields.add(fieldData);
            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : fieldData.getTypeParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    final String expression = parameterTemplateData.getExpression();
                    final NativeTypeInfoTemplateData typeInfo = parameterTemplateData.getTypeInfo();
                    explicitParameters.add(new ExplicitParameterTemplateData(expression, typeInfo));
                }
            }
        }

        templateInstantiation = TemplateInstantiationTemplateData.create(context, tableType);
    }

    public String getRootPackageName()
    {
        return rootPackageName;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public String getRowName()
    {
        return rowName;
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

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<ExplicitParameterTemplateData> getExplicitParameters()
    {
        return explicitParameters;
    }

    public String getSqlConstraint()
    {
        return sqlConstraint;
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
        public FieldTemplateData(JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter, ExpressionFormatter javaSqlExpressionFormatter,
                ExpressionFormatter javaSqlLambdaExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType parentType, Field field) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(fieldTypeInstantiation);

            name = field.getName();

            typeInfo = new NativeTypeInfoTemplateData(nativeType, fieldTypeInstantiation);

            requiresBigInt = (nativeType instanceof NativeIntegralType) ?
                    ((NativeIntegralType)nativeType).requiresBigInt() : false;

            typeParameters = new ArrayList<ParameterTemplateData>();
            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter parameter : parameterizedInstantiation.getInstantiatedParameters())
                {
                    typeParameters.add(new ParameterTemplateData(javaNativeMapper, javaSqlExpressionFormatter,
                            javaSqlLambdaExpressionFormatter, parentType, parameter));
                }
            }

            isVirtual = field.isVirtual();
            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            sqlConstraint = (fieldSqlConstraint == null) ? null :
                javaExpressionFormatter.formatGetter(fieldSqlConstraint.getConstraintExpr());
            isNotNull = !SqlConstraint.isNullAllowed(fieldSqlConstraint);
            isPrimaryKey = parentType.isFieldPrimaryKey(field);

            underlyingTypeInfo = createUnderlyingTypeInfo(javaNativeMapper, fieldBaseType);
            rangeCheckData = createRangeCheckTemplateData(javaNativeMapper, fieldBaseType,
                    javaExpressionFormatter, fieldTypeInstantiation);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
        }

        public String getName()
        {
            return name;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public boolean getRequiresBigInt()
        {
            return requiresBigInt;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public Iterable<ParameterTemplateData> getTypeParameters()
        {
            return typeParameters;
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

        public NativeTypeInfoTemplateData getUnderlyingTypeInfo()
        {
            return underlyingTypeInfo;
        }

        public RangeCheckTemplateData getRangeCheckData()
        {
            return rangeCheckData;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public static class ParameterTemplateData
        {
            public ParameterTemplateData(JavaNativeMapper javaNativeMapper,
                    ExpressionFormatter javaSqlExpressionFormatter,
                    ExpressionFormatter javaSqlLambdaExpressionFormatter, SqlTableType tableType,
                    InstantiatedParameter instantiatedParameter) throws ZserioExtensionException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                expression = javaSqlExpressionFormatter.formatGetter(argumentExpression);
                lambdaExpression = javaSqlLambdaExpressionFormatter.formatGetter(argumentExpression);
                final Parameter parameter = instantiatedParameter.getParameter();
                name = parameter.getName();
                final TypeReference parameterTypeReference = parameter.getTypeReference();
                final JavaNativeType parameterNativeType = javaNativeMapper.getJavaType(parameterTypeReference);
                typeInfo = new NativeTypeInfoTemplateData(parameterNativeType, parameterTypeReference);
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            public String getLambdaExpression()
            {
                return lambdaExpression;
            }

            public String getName()
            {
                return name;
            }

            public NativeTypeInfoTemplateData getTypeInfo()
            {
                return typeInfo;
            }

            private final boolean isExplicit;
            private final String expression;
            private final String lambdaExpression;
            private final String name;
            private final NativeTypeInfoTemplateData typeInfo;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioExtensionException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(
                        field.getTypeInstantiation());
                name = sqlNativeType.getFullName();
                traditionalName = sqlNativeType.getTraditionalName();
                isBlob = sqlNativeType instanceof NativeBlobType;
            }

            public String getName()
            {
                return name;
            }

            public String getTraditionalName()
            {
                return traditionalName;
            }

            public boolean getIsBlob()
            {
                return isBlob;
            }

            private final String name;
            private final String traditionalName;
            private final boolean isBlob;
        }

        private NativeTypeInfoTemplateData createUnderlyingTypeInfo(JavaNativeMapper javaNativeMapper,
                ZserioType fieldBaseType) throws ZserioExtensionException
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

            final JavaNativeType nativeBaseType = javaNativeMapper.getJavaType(baseTypeInstantiation);

            return new NativeTypeInfoTemplateData(nativeBaseType, baseTypeInstantiation);
        }

        private RangeCheckTemplateData createRangeCheckTemplateData(JavaNativeMapper javaNativeMapper,
                ZserioType fieldBaseType, ExpressionFormatter javaExpressionFormatter,
                TypeInstantiation typeInstantiation) throws ZserioExtensionException
        {
            TypeInstantiation rangeCheckInstantiation = typeInstantiation;
            if (fieldBaseType instanceof EnumType)
                rangeCheckInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
            else if (fieldBaseType instanceof BitmaskType)
                rangeCheckInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();

            return new RangeCheckTemplateData(javaNativeMapper, rangeCheckInstantiation,
                    javaExpressionFormatter);
        }

        private final List<ParameterTemplateData> typeParameters;

        private final String name;
        private final NativeTypeInfoTemplateData typeInfo;
        private final boolean requiresBigInt;
        private final boolean isVirtual;
        private final String sqlConstraint;
        private final boolean isNotNull;
        private final boolean isPrimaryKey;
        private final NativeTypeInfoTemplateData underlyingTypeInfo;
        private final RangeCheckTemplateData rangeCheckData;
        private final SqlTypeTemplateData sqlTypeData;
    }

    private final String rootPackageName;
    private final boolean withValidationCode;
    private final String rowName;
    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;
    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters =
            new TreeSet<ExplicitParameterTemplateData>();
    private final TemplateInstantiationTemplateData templateInstantiation;
}
