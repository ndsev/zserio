package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.sql.SqlNativeTypeMapper;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.SqlNativeType;
import zserio.emit.python.types.PythonNativeType;
import zserio.tools.HashUtil;

public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType sqlTableType)
            throws ZserioEmitException
    {
        super(context, sqlTableType);

        final SqlConstraint sqlConstraintType = sqlTableType.getSqlConstraint();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        sqlConstraint = (sqlConstraintType == null) ? null :
            pythonExpressionFormatter.formatGetter(sqlConstraintType.getTranslatedConstraintExpr());
        virtualTableUsing = sqlTableType.getVirtualTableUsingString();
        needsTypesInSchema = sqlTableType.needsTypesInSchema();
        isWithoutRowId = sqlTableType.isWithoutRowId();

        final PythonNativeTypeMapper pythonNativeTypeMapper = context.getPythonNativeTypeMapper();
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        for (Field field: sqlTableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(pythonNativeTypeMapper,
                    pythonExpressionFormatter, context.getPythonSqlIndirectExpressionFormatter(this),
                    sqlNativeTypeMapper, sqlTableType, field, this);
            fields.add(fieldData);
            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : fieldData.getParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    explicitParameters.add(new ExplicitParameterTemplateData(
                            parameterTemplateData.getExpression()));
                }
            }
        }
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

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<ExplicitParameterTemplateData> getExplicitParameters()
    {
        return explicitParameters;
    }

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(String expression)
        {
            this.expression = expression;
        }

        public String getExpression()
        {
            return expression;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            return expression.compareTo(other.expression);
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
            return hash;
        }

        private final String expression;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(PythonNativeTypeMapper pythonNativeTypeMapper,
                ExpressionFormatter pythonExpressionFormatter,
                ExpressionFormatter pythonSqlIndirectExpressionFormatter,
                SqlNativeTypeMapper sqlNativeTypeMapper, SqlTableType parentType, Field field,
                ImportCollector importCollector) throws ZserioEmitException
        {
            final ZserioType baseType = TypeReference.resolveBaseType(field.getFieldType());
            final PythonNativeType nativeType = pythonNativeTypeMapper.getPythonType(baseType);
            importCollector.importType(nativeType);

            name = field.getName();
            pythonTypeName = nativeType.getFullName();

            isVirtual = field.getIsVirtual();
            parameters = new ArrayList<ParameterTemplateData>();

            if (baseType instanceof TypeInstantiation)
            {
                final TypeInstantiation typeInstantiation = ((TypeInstantiation)baseType);
                for (TypeInstantiation.InstantiatedParameter instantiatedParameter :
                        typeInstantiation.getInstantiatedParameters())
                {
                    parameters.add(new ParameterTemplateData(pythonSqlIndirectExpressionFormatter, parentType,
                            instantiatedParameter));
                }
            }

            final SqlConstraint sqlConstraintType = field.getSqlConstraint();
            final Expression sqlConstraintExpr = sqlConstraintType.getTranslatedFieldConstraintExpr();
            sqlConstraint = (sqlConstraintExpr == null) ? null :
                pythonExpressionFormatter.formatGetter(sqlConstraintExpr);

            enumData = (baseType instanceof EnumType) ? new EnumTemplateData(nativeType) : null;
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
        }

        public String getName()
        {
            return name;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public Iterable<ParameterTemplateData> getParameters()
        {
            return parameters;
        }

        public String getSqlConstraint()
        {
            return sqlConstraint;
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
            public EnumTemplateData(PythonNativeType enumNativeType)
            {
                pythonTypeName = enumNativeType.getFullName();
            }

            public String getPythonTypeName()
            {
                return pythonTypeName;
            }

            private final String pythonTypeName;
        }

        public static class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioEmitException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(field.getFieldType());
                name = sqlNativeType.getFullName();
                isBlob = sqlNativeType instanceof NativeBlobType;
            }

            public String getName()
            {
                return name;
            }

            public boolean getIsBlob()
            {
                return isBlob;
            }

            private final String name;
            private final boolean isBlob;
        }

        public static class ParameterTemplateData
        {
            public ParameterTemplateData(ExpressionFormatter pythonSqlIndirectExpressionFormatter,
                    SqlTableType tableType, TypeInstantiation.InstantiatedParameter instantiatedParameter)
                            throws ZserioEmitException
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                expression = pythonSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            private final boolean isExplicit;
            private final String expression;
        }

        private final String name;
        private final String pythonTypeName;

        private final boolean isVirtual;
        private final List<ParameterTemplateData> parameters;
        private final String sqlConstraint;
        private final EnumTemplateData enumData;
        private final SqlTypeTemplateData sqlTypeData;
    }

    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;

    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters =
            new TreeSet<ExplicitParameterTemplateData>();
}
