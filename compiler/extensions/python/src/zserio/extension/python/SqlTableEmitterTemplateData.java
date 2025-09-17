package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FixedSizeType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
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
public final class SqlTableEmitterTemplateData extends UserTypeTemplateData
{
    public SqlTableEmitterTemplateData(TemplateDataContext context, SqlTableType tableType)
            throws ZserioExtensionException
    {
        super(context, tableType, tableType);

        importPackage("typing");
        importPackage("apsw");

        final SqlConstraint tableSqlConstraint = tableType.getSqlConstraint();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        sqlConstraint = (tableSqlConstraint == null)
                ? null
                : pythonExpressionFormatter.formatGetter(tableSqlConstraint.getConstraintExpr());
        virtualTableUsing = tableType.getVirtualTableUsingString();
        needsTypesInSchema = tableType.needsTypesInSchema();
        isWithoutRowId = tableType.isWithoutRowId();

        for (Field field : tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(context, tableType, field, this);
            fields.add(fieldData);
            for (FieldTemplateData.ParameterTemplateData parameterTemplateData : fieldData.getParameters())
            {
                if (parameterTemplateData.getIsExplicit())
                {
                    explicitParameters.add(new ExplicitParameterTemplateData(parameterTemplateData));
                }
            }
        }

        templateInstantiation = TemplateInstantiationTemplateData.create(context, tableType, this);
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

    public TemplateInstantiationTemplateData getTemplateInstantiation()
    {
        return templateInstantiation;
    }

    public static final class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(ParameterTemplateData parameterTemplateData)
        {
            expression = parameterTemplateData.getExpression();
            typeInfo = parameterTemplateData.getTypeInfo();
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

    public static final class FieldTemplateData
    {
        public FieldTemplateData(TemplateDataContext context, SqlTableType parentType, Field field,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
            final PythonNativeType nativeType = pythonNativeMapper.getPythonType(fieldTypeInstantiation);
            importCollector.importType(nativeType);

            name = field.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            upperCaseName = PythonSymbolConverter.enumItemToSymbol(name, false);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, fieldTypeInstantiation);

            isVirtual = field.isVirtual();
            parameters = new ArrayList<ParameterTemplateData>();

            if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                for (InstantiatedParameter instantiatedParameter :
                        parameterizedInstantiation.getInstantiatedParameters())
                {
                    parameters.add(new ParameterTemplateData(
                            context, parentType, instantiatedParameter, importCollector));
                }
            }

            final SqlConstraint fieldSqlConstraint = field.getSqlConstraint();
            final ExpressionFormatter pythonExpressionFormatter =
                    context.getPythonExpressionFormatter(importCollector);
            sqlConstraint = (fieldSqlConstraint == null)
                    ? null
                    : pythonExpressionFormatter.formatGetter(fieldSqlConstraint.getConstraintExpr());

            lambdaBitSize = createBitSize(fieldTypeInstantiation, pythonExpressionFormatter);
            final SqlNativeTypeMapper sqlNativeTypeMapper = new SqlNativeTypeMapper();
            sqlTypeData = new SqlTypeTemplateData(sqlNativeTypeMapper, field);

            docComments = DocCommentsDataCreator.createData(context, field);
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public String getUpperCaseName()
        {
            return upperCaseName;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
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

        public String getLambdaBitSize()
        {
            return lambdaBitSize;
        }

        public SqlTypeTemplateData getSqlTypeData()
        {
            return sqlTypeData;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public static final class SqlTypeTemplateData
        {
            public SqlTypeTemplateData(SqlNativeTypeMapper sqlNativeTypeMapper, Field field)
                    throws ZserioExtensionException
            {
                final SqlNativeType sqlNativeType =
                        sqlNativeTypeMapper.getSqlType(field.getTypeInstantiation());
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

        public static final class ParameterTemplateData
        {
            public ParameterTemplateData(TemplateDataContext context, SqlTableType tableType,
                    InstantiatedParameter instantiatedParameter, ImportCollector importCollector)
                    throws ZserioExtensionException
            {
                final Parameter parameter = instantiatedParameter.getParameter();
                final TypeReference referencedType = parameter.getTypeReference();
                final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
                final PythonNativeType parameterNativeType = pythonNativeMapper.getPythonType(referencedType);
                importCollector.importType(parameterNativeType);

                typeInfo = new NativeTypeInfoTemplateData(parameterNativeType, referencedType);

                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                isExplicit = argumentExpression.isExplicitVariable();
                final ExpressionFormatter pythonSqlIndirectExpressionFormatter =
                        context.getPythonSqlIndirectExpressionFormatter(importCollector);
                expression = pythonSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
                final ExpressionFormatter pythonExpressionFormatter =
                        context.getPythonExpressionFormatter(importCollector);
                lambdaExpression = pythonExpressionFormatter.formatGetter(argumentExpression);
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

            public String getLambdaExpression()
            {
                return lambdaExpression;
            }

            private final NativeTypeInfoTemplateData typeInfo;
            private final boolean isExplicit;
            private final String expression;
            private final String lambdaExpression;
        }

        private static String createBitSize(TypeInstantiation typeInstantiation,
                ExpressionFormatter pythonSqlIndirectExpressionFormatter) throws ZserioExtensionException
        {
            String bitSizeOfValue = null;
            if (typeInstantiation.getBaseType() instanceof FixedSizeType)
            {
                return PythonLiteralFormatter.formatDecimalLiteral(
                        ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            }
            else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)typeInstantiation;
                bitSizeOfValue = pythonSqlIndirectExpressionFormatter.formatGetter(
                        dynamicBitFieldInstantiation.getLengthExpression());
            }

            return bitSizeOfValue;
        }

        private final String name;
        private final String snakeCaseName;
        private final String upperCaseName;
        private final NativeTypeInfoTemplateData typeInfo;
        private final boolean isVirtual;
        private final List<ParameterTemplateData> parameters;
        private final String sqlConstraint;
        private final String lambdaBitSize;
        private final SqlTypeTemplateData sqlTypeData;
        private final DocCommentsTemplateData docComments;
    }

    private final String sqlConstraint;
    private final String virtualTableUsing;
    private final boolean needsTypesInSchema;
    private final boolean isWithoutRowId;

    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final SortedSet<ExplicitParameterTemplateData> explicitParameters =
            new TreeSet<ExplicitParameterTemplateData>();

    private final TemplateInstantiationTemplateData templateInstantiation;
}
