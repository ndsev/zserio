package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.common.sql.SqlNativeTypeMapper;
import zserio.extension.common.sql.types.NativeBlobType;
import zserio.extension.common.sql.types.SqlNativeType;
import zserio.extension.python.SqlTableEmitterTemplateData.FieldTemplateData.ParameterTemplateData;
import zserio.extension.python.types.PythonNativeType;
import zserio.tools.HashUtil;

/**
 * FreeMarker template data for SqlTableEmitter.
 */
public class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType sqlTableType)
            throws ZserioExtensionException
    {
        super(context, sqlTableType);

        importPackage("typing");
        importPackage("apsw");

        final SqlConstraint tableSqlConstraint = sqlTableType.getSqlConstraint();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        sqlConstraint = (tableSqlConstraint == null) ? null :
            pythonExpressionFormatter.formatGetter(tableSqlConstraint.getConstraintExpr());
        virtualTableUsing = sqlTableType.getVirtualTableUsingString();
        needsTypesInSchema = sqlTableType.needsTypesInSchema();
        isWithoutRowId = sqlTableType.isWithoutRowId();

        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
        for (Field field: sqlTableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(pythonNativeMapper,
                    pythonExpressionFormatter, context.getPythonSqlIndirectExpressionFormatter(this),
                    sqlNativeTypeMapper, sqlTableType, field, this);
            fields.add(fieldData);
            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : fieldData.getParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    explicitParameters.add(new ExplicitParameterTemplateData(parameterTemplateData));
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
        public ExplicitParameterTemplateData(ParameterTemplateData parameterTemplateData)
        {
            expression = parameterTemplateData.getExpression();
            pythonTypeName = parameterTemplateData.getPythonTypeName();
        }

        public String getExpression()
        {
            return expression;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        @Override
        public int compareTo(ExplicitParameterTemplateData other)
        {
            int result = expression.compareTo(other.expression);
            if (result != 0)
                return result;

            return pythonTypeName.compareTo(other.pythonTypeName);
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
            hash = HashUtil.hash(hash, pythonTypeName);

            return hash;
        }

        private final String expression;
        private final String pythonTypeName;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(PythonNativeMapper pythonNativeMapper,
                ExpressionFormatter pythonExpressionFormatter,
                ExpressionFormatter pythonSqlIndirectExpressionFormatter,
                SqlNativeTypeMapper sqlNativeTypeMapper, SqlTableType parentType, Field field,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final ZserioType fieldBaseType = fieldTypeInstantiation.getBaseType();
            final PythonNativeType nativeType = pythonNativeMapper.getPythonType(fieldTypeInstantiation);
            importCollector.importType(nativeType);

            name = field.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            pythonTypeName = PythonFullNameFormatter.getFullName(nativeType);

            isVirtual = field.getIsVirtual();
            parameters = new ArrayList<ParameterTemplateData>();

            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter instantiatedParameter :
                        parameterizedInstantiation.getInstantiatedParameters())
                {
                    parameters.add(new ParameterTemplateData(pythonNativeMapper,
                            pythonSqlIndirectExpressionFormatter, parentType, instantiatedParameter,
                            importCollector));
                }
            }

            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            sqlConstraint = (fieldSqlConstraint == null) ? null :
                    pythonExpressionFormatter.formatGetter(fieldSqlConstraint.getConstraintExpr());

            enumData = (fieldBaseType instanceof EnumType) ? new EnumTemplateData(nativeType) : null;
            bitmaskData = (fieldBaseType instanceof BitmaskType) ? new BitmaskTemplateData(nativeType) : null;
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
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

        public BitmaskTemplateData getBitmaskData()
        {
            return bitmaskData;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public static class EnumTemplateData
        {
            public EnumTemplateData(PythonNativeType enumNativeType)
            {
                pythonTypeName = PythonFullNameFormatter.getFullName(enumNativeType);
            }

            public String getPythonTypeName()
            {
                return pythonTypeName;
            }

            private final String pythonTypeName;
        }

        public static class BitmaskTemplateData
        {
            public BitmaskTemplateData(PythonNativeType bitmaskNativeType)
            {
                pythonTypeName = PythonFullNameFormatter.getFullName(bitmaskNativeType);
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
                    throws ZserioExtensionException
            {
                final SqlNativeType sqlNativeType = sqlNativeTypeMapper.getSqlType(
                        field.getTypeInstantiation());
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
            public ParameterTemplateData(PythonNativeMapper pythonNativeMapper,
                    ExpressionFormatter pythonSqlIndirectExpressionFormatter, SqlTableType tableType,
                    InstantiatedParameter instantiatedParameter, ImportCollector importCollector)
                            throws ZserioExtensionException
            {
                final Parameter parameter = instantiatedParameter.getParameter();
                final PythonNativeType parameterNativeType =
                        pythonNativeMapper.getPythonType(parameter.getTypeReference());
                importCollector.importType(parameterNativeType);

                pythonTypeName = PythonFullNameFormatter.getFullName(parameterNativeType);

                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                expression = pythonSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
            }

            public String getPythonTypeName()
            {
                return pythonTypeName;
            }

            public boolean getIsExplicit()
            {
                return isExplicit;
            }

            public String getExpression()
            {
                return expression;
            }

            private final String pythonTypeName;
            private final boolean isExplicit;
            private final String expression;
        }

        private final String name;
        private final String snakeCaseName;
        private final String pythonTypeName;

        private final boolean isVirtual;
        private final List<ParameterTemplateData> parameters;
        private final String sqlConstraint;
        private final EnumTemplateData enumData;
        private final BitmaskTemplateData bitmaskData;
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
