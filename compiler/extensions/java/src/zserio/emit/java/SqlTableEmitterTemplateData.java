package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Parameter;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.sql.SqlNativeTypeMapper;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.SqlNativeType;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeEnumType;
import zserio.emit.java.types.NativeIntegralType;

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

        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        for (Field field: tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(javaNativeTypeMapper,
                    javaExpressionFormatter, context.getJavaSqlIndirectExpressionFormatter(),
                    sqlNativeTypeMapper, tableType, field);
            fields.add(fieldData);
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

    public String getSqlConstraint()
    {
        return sqlConstraint;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(JavaNativeTypeMapper javaNativeTypeMapper,
                ExpressionFormatter javaExpressionFormatter,
                ExpressionFormatter javaSqlIndirectExpressionFormatter, SqlNativeTypeMapper sqlNativeTypeMapper,
                SqlTableType parentType, Field field) throws ZserioEmitException
        {
            final ZserioType baseType = TypeReference.resolveBaseType(field.getFieldType());
            final JavaNativeType nativeType = javaNativeTypeMapper.getJavaType(baseType);

            name = field.getName();
            javaTypeName = nativeType.getFullName();
            requiresBigInt = (nativeType instanceof NativeIntegralType) ?
                    ((NativeIntegralType)nativeType).requiresBigInt() : false;
            typeParameters = new ArrayList<SqlTableParameterTemplateData>();

            if (baseType instanceof TypeInstantiation)
            {
                final TypeInstantiation typeInstantiation = ((TypeInstantiation)baseType);
                for (TypeInstantiation.InstantiatedParameter instantiatedParameter :
                        typeInstantiation.getInstantiatedParameters())
                {
                    typeParameters.add(new SqlTableParameterTemplateData(javaNativeTypeMapper,
                            javaSqlIndirectExpressionFormatter, parentType, instantiatedParameter));
                }
            }

            isVirtual = field.getIsVirtual();
            final SqlConstraint sqlConstraintType = field.getSqlConstraint();
            final Expression sqlConstraintExpr = sqlConstraintType.getTranslatedFieldConstraintExpr();
            sqlConstraint = (sqlConstraintExpr == null) ? null :
                javaExpressionFormatter.formatGetter(sqlConstraintExpr);
            isNotNull = !sqlConstraintType.isNullAllowed();
            isPrimaryKey = parentType.isFieldPrimaryKey(field);

            // enumerations are rangeable for SQL
            enumData = createEnumTemplateData(nativeType);
            final ZserioType rangeCheckType = (enumData != null) ?
                    ((EnumType)baseType).getIntegerBaseType() : baseType;
            rangeCheckData = new RangeCheckTemplateData(javaNativeTypeMapper, rangeCheckType,
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

        public boolean getRequiresBigInt()
        {
            return requiresBigInt;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public Iterable<SqlTableParameterTemplateData> getTypeParameters()
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
            public ParameterTemplateData(JavaNativeTypeMapper javaNativeTypeMapper,
                    ExpressionFormatter javaExpressionFormatter, Parameter parameter, Expression argument,
                    SqlTableType tableType) throws ZserioEmitException
            {
                name = parameter.getName();
                instantiationName = (!argument.isExplicitVariable()) ? null :
                    javaExpressionFormatter.formatGetter(argument);
                javaTypeName = javaNativeTypeMapper.getJavaType(parameter.getParameterType()).getFullName();
            }

            /**
             * Returns the name of the parameter (it's the name used in the definition of the type).
             * @return
             */
            public String getName()
            {
                return name;
            }

            /**
             * Returns the name used in the instantiation of the type.
             *
             * This works only for explicit parameters. For non-explicit parameters null is returned.
             */
            public String getInstantiationName()
            {
                return instantiationName;
            }

            public String getJavaTypeName()
            {
                return javaTypeName;
            }

            private final String name;
            private final String instantiationName;
            private final String javaTypeName;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(NativeEnumType nativeEnumType)
            {
                baseJavaTypeName = nativeEnumType.getBaseType().getFullName();
            }

            public String getBaseJavaTypeName()
            {
                return baseJavaTypeName;
            }

            private final String baseJavaTypeName;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioEmitException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(field.getFieldType());
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

            private final String    name;
            private final String    traditionalName;
            private final boolean   isBlob;
        }

        private EnumTemplateData createEnumTemplateData(JavaNativeType nativeType)
        {
            if (!(nativeType instanceof NativeEnumType))
                return null;

            return new EnumTemplateData((NativeEnumType)nativeType);
        }

        private final List<SqlTableParameterTemplateData>   typeParameters;

        private final String                    name;
        private final String                    javaTypeName;
        private final boolean                   requiresBigInt;
        private final boolean                   isVirtual;
        private final String                    sqlConstraint;
        private final boolean                   isNotNull;
        private final boolean                   isPrimaryKey;
        private final EnumTemplateData          enumData;
        private final RangeCheckTemplateData    rangeCheckData;
        private final SqlTypeTemplateData       sqlTypeData;
    }

    private final String                    rootPackageName;
    private final boolean                   withValidationCode;
    private final String                    rowName;
    private final String                    sqlConstraint;
    private final String                    virtualTableUsing;
    private final boolean                   needsTypesInSchema;
    private final boolean                   isWithoutRowId;
    private final List<FieldTemplateData>   fields = new ArrayList<FieldTemplateData>();
}
