package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.sql.SqlNativeTypeMapper;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.NativeIntegerType;
import zserio.emit.common.sql.types.NativeRealType;
import zserio.emit.common.sql.types.SqlNativeType;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeEnumType;

public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType, String tableRowName)
    {
        super(context, tableType);

        rowName = tableRowName;

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
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
        for (Field tableField : tableFields)
        {
            final FieldTemplateData field = new FieldTemplateData(cppNativeTypeMapper, cppExpressionFormatter,
                    cppSqlIndirectExpressionFormatter, sqlNativeTypeMapper, tableType, tableField, this);
            fields.add(field);
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

    public static class FieldTemplateData
    {
        public FieldTemplateData(CppNativeTypeMapper cppNativeTypeMapper,
                ExpressionFormatter cppExpressionFormatter,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType table, Field field, IncludeCollector includeCollector)
        {
            final ZserioType fieldType = field.getFieldType();
            final CppNativeType nativeFieldType = cppNativeTypeMapper.getCppType(fieldType);
            includeCollector.addHeaderIncludesForType(nativeFieldType);

            name = field.getName();
            cppTypeName = nativeFieldType.getFullName();
            zserioTypeName = ZserioTypeUtil.getFullName(fieldType);
            final Expression sqlConstraintExpr = field.getSqlConstraint().getTranslatedFieldConstraintExpr();
            sqlConstraint = (sqlConstraintExpr == null) ? null :
                cppExpressionFormatter.formatGetter(sqlConstraintExpr);
            isVirtual = field.getIsVirtual();

            typeParameters = new ArrayList<SqlTableParameterTemplateData>();
            final List<TypeInstantiation.InstantiatedParameter> parameters =
                    field.getInstantiatedParameters();
            for (TypeInstantiation.InstantiatedParameter parameter : parameters)
            {
                typeParameters.add(new SqlTableParameterTemplateData(cppNativeTypeMapper,
                        cppExpressionFormatter, cppSqlIndirectExpressionFormatter, table, field, parameter,
                        includeCollector));
            }

            enumData = createEnumTemplateData(nativeFieldType);
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

        public Iterable<SqlTableParameterTemplateData> getTypeParameters()
        {
            return typeParameters;
        }

        public EnumTemplateData getEnumData()
        {
            return enumData;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
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
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(field.getFieldType());
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

            private final String    name;
            private final boolean   isBlob;
            private final boolean   isInteger;
            private final boolean   isReal;
        }

        private EnumTemplateData createEnumTemplateData(CppNativeType nativeType)
        {
            if (!(nativeType instanceof NativeEnumType))
                return null;

            return new EnumTemplateData((NativeEnumType)nativeType);
        }

        private final String                name;
        private final String                cppTypeName;
        private final String                zserioTypeName;
        private final String                sqlConstraint;
        private final boolean               isVirtual;
        private final List<SqlTableParameterTemplateData>   typeParameters;
        private final EnumTemplateData      enumData;
        private final SqlTypeTemplateData   sqlTypeData;
    }

    private final String                    rowName;
    private final List<FieldTemplateData>   fields;
    private final String                    sqlConstraint;
    private final String                    virtualTableUsing;
    private final boolean                   needsTypesInSchema;
    private final boolean                   isWithoutRowId;
}
