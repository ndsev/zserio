package zserio.emit.java;

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
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.sql.SqlNativeTypeMapper;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.SqlNativeType;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeBitmaskType;
import zserio.emit.java.types.NativeEnumType;
import zserio.emit.java.types.NativeIntegralType;
import zserio.tools.HashUtil;

public final class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType, String tableRowName)
            throws ZserioEmitException
    {
        super(context, tableType);

        rootPackageName = context.getJavaRootPackageName();
        this.withValidationCode = context.getWithValidationCode();

        rowName = tableRowName;
        final SqlConstraint sqlConstraintType = tableType.getSqlConstraint();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        sqlConstraint = (sqlConstraintType == null) ? null :
            javaExpressionFormatter.formatGetter(sqlConstraintType.getTranslatedConstraintExpr());
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        for (Field field: tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(javaNativeMapper,
                    javaExpressionFormatter, context.getJavaSqlIndirectExpressionFormatter(),
                    sqlNativeTypeMapper, tableType, field);
            fields.add(fieldData);
            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : fieldData.getTypeParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    final String expression = parameterTemplateData.getExpression();
                    final String javaTypeFullName = parameterTemplateData.getJavaTypeFullName();
                    explicitParameters.add(new ExplicitParameterTemplateData(expression, javaTypeFullName));
                }
            }
        }
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

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(String expression, String javaTypeFullName)
        {
            this.expression = expression;
            this.javaTypeFullName = javaTypeFullName;
        }

        public String getExpression()
        {
            return expression;
        }

        public String getJavaTypeFullName()
        {
            return javaTypeFullName;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            int result = expression.compareTo(other.expression);
            if (result != 0)
                return result;

            return javaTypeFullName.compareTo(other.javaTypeFullName);
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
            hash = HashUtil.hash(hash, javaTypeFullName);
            return hash;
        }

        private final String expression;
        private final String javaTypeFullName;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(JavaNativeMapper javaNativeMapper,
                ExpressionFormatter javaExpressionFormatter,
                ExpressionFormatter javaSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType parentType, Field field) throws ZserioEmitException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(fieldTypeInstantiation);

            name = field.getName();
            javaTypeName = nativeType.getName();
            javaTypeFullName = nativeType.getFullName();
            requiresBigInt = (nativeType instanceof NativeIntegralType) ?
                    ((NativeIntegralType)nativeType).requiresBigInt() : false;

            typeParameters = new ArrayList<ParameterTemplateData>();
            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter parameter : parameterizedInstantiation.getInstantiatedParameters())
                {
                    typeParameters.add(new ParameterTemplateData(javaNativeMapper,
                            javaSqlIndirectExpressionFormatter, parentType, parameter));
                }
            }

            isVirtual = field.getIsVirtual();
            final SqlConstraint sqlConstraintType = field.getSqlConstraint();
            final Expression sqlConstraintExpr = sqlConstraintType.getTranslatedFieldConstraintExpr();
            sqlConstraint = (sqlConstraintExpr == null) ? null :
                javaExpressionFormatter.formatGetter(sqlConstraintExpr);
            isNotNull = !sqlConstraintType.isNullAllowed();
            isPrimaryKey = parentType.isFieldPrimaryKey(field);

            // enumerations and bitmasks are rangeable for SQL
            enumData = createEnumTemplateData(nativeType);
            bitmaskData = createBitmaskTemplateData(nativeType);
            TypeInstantiation rangeCheckInstantiation = fieldTypeInstantiation;
            if (enumData != null)
                rangeCheckInstantiation = ((EnumType)fieldBaseType).getTypeInstantiation();
            else if (bitmaskData != null)
                rangeCheckInstantiation = ((BitmaskType)fieldBaseType).getTypeInstantiation();
            rangeCheckData = new RangeCheckTemplateData(javaNativeMapper, rangeCheckInstantiation,
                    javaExpressionFormatter);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
        }

        public String getName()
        {
            return name;
        }

        public String getJavaTypeName()
        {
            return javaTypeName;
        }

        public String getJavaTypeFullName()
        {
            return javaTypeFullName;
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

        public EnumTemplateData getEnumData()
        {
            return enumData;
        }

        public BitmaskTemplateData getBitmaskData()
        {
            return bitmaskData;
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
                    ExpressionFormatter javaSqlIndirectExpressionFormatter, SqlTableType tableType,
                    InstantiatedParameter instantiatedParameter) throws ZserioEmitException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                expression = javaSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
                final Parameter parameter = instantiatedParameter.getParameter();
                definitionName = parameter.getName();
                javaTypeFullName = javaNativeMapper.getJavaType(parameter.getTypeReference()).getFullName();
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            public String getDefinitionName()
            {
                return definitionName;
            }

            public String getJavaTypeFullName()
            {
                return javaTypeFullName;
            }

            private final boolean isExplicit;
            private final String expression;
            private final String definitionName;
            private final String javaTypeFullName;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(NativeEnumType nativeEnumType)
            {
                baseJavaTypeName = nativeEnumType.getBaseType().getName();
                baseJavaTypeFullName = nativeEnumType.getBaseType().getFullName();
            }

            public String getBaseJavaTypeName()
            {
                return baseJavaTypeName;
            }

            public String getBaseJavaTypeFullName()
            {
                return baseJavaTypeFullName;
            }

            private final String baseJavaTypeName;
            private final String baseJavaTypeFullName;
        }

        public static class BitmaskTemplateData
        {
            public BitmaskTemplateData(NativeBitmaskType nativeBitmaskType)
            {
                baseJavaTypeName = nativeBitmaskType.getBaseType().getName();
                baseJavaTypeFullName = nativeBitmaskType.getBaseType().getFullName();
            }

            public String getBaseJavaTypeName()
            {
                return baseJavaTypeName;
            }

            public String getBaseJavaTypeFullName()
            {
                return baseJavaTypeFullName;
            }

            private final String baseJavaTypeName;
            private final String baseJavaTypeFullName;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioEmitException
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

        private EnumTemplateData createEnumTemplateData(JavaNativeType nativeType)
        {
            if (!(nativeType instanceof NativeEnumType))
                return null;

            return new EnumTemplateData((NativeEnumType)nativeType);
        }

        private BitmaskTemplateData createBitmaskTemplateData(JavaNativeType nativeType)
        {
            if (!(nativeType instanceof NativeBitmaskType))
                return null;

            return new BitmaskTemplateData((NativeBitmaskType)nativeType);
        }

        private final List<ParameterTemplateData> typeParameters;

        private final String name;
        private final String javaTypeName;
        private final String javaTypeFullName;
        private final boolean requiresBigInt;
        private final boolean isVirtual;
        private final String sqlConstraint;
        private final boolean isNotNull;
        private final boolean isPrimaryKey;
        private final EnumTemplateData enumData;
        private final BitmaskTemplateData bitmaskData;
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
}
