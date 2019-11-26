package zserio.emit.cpp98;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BooleanType;
import zserio.ast.EnumType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.sql.SqlNativeTypeMapper;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.NativeIntegerType;
import zserio.emit.common.sql.types.NativeRealType;
import zserio.emit.common.sql.types.SqlNativeType;
import zserio.emit.cpp98.types.CppNativeType;
import zserio.emit.cpp98.types.NativeEnumType;
import zserio.tools.HashUtil;

public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType, String tableRowName)
            throws ZserioEmitException
    {
        super(context, tableType);

        rowName = tableRowName;

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        final SqlConstraint sqlConstraintType = tableType.getSqlConstraint();
        sqlConstraint = (sqlConstraintType == null) ? null :
            cppExpressionFormatter.formatGetter(sqlConstraintType.getTranslatedConstraintExpr());
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        final List<Field> tableFields = tableType.getFields();
        fields = new ArrayList<FieldTemplateData>(tableFields.size());
        final ExpressionFormatter cppSqlIndirectExpressionFormatter =
                context.getSqlIndirectExpressionFormatter(this);
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        explicitParameters = new TreeSet<ExplicitParameterTemplateData>();
        for (Field tableField : tableFields)
        {
            final FieldTemplateData field = new FieldTemplateData(cppNativeMapper, cppExpressionFormatter,
                    cppSqlIndirectExpressionFormatter, sqlNativeTypeMapper, tableType, tableField, this);
            fields.add(field);
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
            }
        }
    }

    public String getRowName()
    {
        return rowName;
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
                ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType table, Field field, IncludeCollector includeCollector) throws ZserioEmitException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final CppNativeType nativeFieldType = cppNativeMapper.getCppType(fieldTypeInstantiation);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            cppTypeName = nativeFieldType.getFullName();
            zserioTypeName = ZserioTypeUtil.getFullName(fieldTypeInstantiation.getType());
            final Expression sqlConstraintExpr = field.getSqlConstraint().getTranslatedFieldConstraintExpr();
            sqlConstraint = (sqlConstraintExpr == null) ? null :
                cppExpressionFormatter.formatGetter(sqlConstraintExpr);
            isVirtual = field.getIsVirtual();

            typeParameters = new ArrayList<ParameterTemplateData>();
            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter parameter : parameterizedInstantiation.getInstantiatedParameters())
                {
                    typeParameters.add(new ParameterTemplateData(cppNativeMapper,
                            cppSqlIndirectExpressionFormatter, table, field, parameter, includeCollector));
                }
            }

            isBoolean = fieldBaseType instanceof BooleanType;
            enumData = createEnumTemplateData(cppNativeMapper, fieldBaseType);
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
        }

        public String getName()
        {
            return name;
        }

        public String getCppTypeName()
        {
            return cppTypeName;
        }

        public String getZserioTypeName()
        {
            return zserioTypeName;
        }

        public String getSqlConstraint()
        {
            return sqlConstraint;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public Iterable<ParameterTemplateData> getTypeParameters()
        {
            return typeParameters;
        }

        public boolean getIsBoolean()
        {
            return isBoolean;
        }

        public EnumTemplateData getEnumData()
        {
            return enumData;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public static class ParameterTemplateData
        {
            public ParameterTemplateData(CppNativeMapper cppNativeMapper,
                    ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlTableType tableType, Field field,
                    InstantiatedParameter instantiatedParameter,
                    IncludeCollector includeCollector) throws ZserioEmitException
            {
                final Parameter parameter = instantiatedParameter.getParameter();
                final CppNativeType parameterNativeType =
                        cppNativeMapper.getCppType(parameter.getTypeReference());
                includeCollector.addHeaderIncludesForType(parameterNativeType);

                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                tableName = tableType.getName();
                fieldName = field.getName();
                definitionName = parameter.getName();
                cppTypeName = parameterNativeType.getFullName();
                isSimpleType = parameterNativeType.isSimpleType();
                expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
            }

            public String getTableName()
            {
                return tableName;
            }

            public String getFieldName()
            {
                return fieldName;
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

            private final String tableName;
            private final String fieldName;
            private final String definitionName;
            private final String cppTypeName;
            private final boolean isSimpleType;
            private final boolean isExplicit;
            private final String expression;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(NativeEnumType nativeEnumType)
            {
                baseCppTypeName = nativeEnumType.getBaseType().getFullName();
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
                    throws ZserioEmitException
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

            private final String  name;
            private final boolean isBlob;
            private final boolean isInteger;
            private final boolean isReal;
        }

        private EnumTemplateData createEnumTemplateData(CppNativeMapper cppNativeMapper,
                ZserioType fieldBaseType) throws ZserioEmitException
        {
            if (!(fieldBaseType instanceof EnumType))
                return null;

            final CppNativeType nativeEnumType = cppNativeMapper.getCppType(fieldBaseType);
            return new EnumTemplateData((NativeEnumType)nativeEnumType);
        }

        private final String name;
        private final String cppTypeName;
        private final String zserioTypeName;
        private final String sqlConstraint;
        private final boolean isVirtual;
        private final List<ParameterTemplateData> typeParameters;
        private final boolean isBoolean;
        private final EnumTemplateData enumData;
        private final SqlTypeTemplateData sqlTypeData;
    }

    private final String rowName;
    private final List<FieldTemplateData> fields;
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters;
    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;
}
