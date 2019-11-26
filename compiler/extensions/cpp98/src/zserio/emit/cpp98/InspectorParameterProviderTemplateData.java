package zserio.emit.cpp98;

import java.util.List;
import java.util.TreeSet;

import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.CppNativeType;
import zserio.tools.HashUtil;

public class InspectorParameterProviderTemplateData extends CppTemplateData
{
    public InspectorParameterProviderTemplateData(TemplateDataContext context, List<SqlTableType> sqlTableTypes)
            throws ZserioEmitException
    {
        super(context);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppSqlIndirectExpressionFormatter =
                context.getSqlIndirectExpressionFormatter(this);

        for (SqlTableType sqlTableType : sqlTableTypes)
        {
            for (Field field : sqlTableType.getFields())
            {
                final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
                if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
                {
                    final ParameterizedTypeInstantiation parameterizedTypeInstantiation =
                            (ParameterizedTypeInstantiation)fieldTypeInstantiation;
                    final List<InstantiatedParameter> instantiatedParameters =
                            parameterizedTypeInstantiation.getInstantiatedParameters();
                    for (InstantiatedParameter parameter : instantiatedParameters)
                    {
                        if (parameter.getArgumentExpression().isExplicitVariable())
                        {
                            explicitParameters.add(new ExplicitParameterTemplateData(cppNativeMapper,
                                    cppSqlIndirectExpressionFormatter, sqlTableType, field, parameter, this));
                        }
                        else
                        {
                            parameters.add(new ParameterTemplateData(cppNativeMapper, sqlTableType, field,
                                    parameter, this));
                        }
                    }
                }
            }
        }
    }

    public Iterable<ParameterTemplateData> getParameters()
    {
        return parameters;
    }

    public Iterable<ExplicitParameterTemplateData> getExplicitParameters()
    {
        return explicitParameters;
    }

    public static class ParameterTemplateData implements Comparable<ParameterTemplateData>
    {
        public ParameterTemplateData(CppNativeMapper cppNativeMapper, SqlTableType tableType,
                Field field, InstantiatedParameter instantiatedParameter,
                IncludeCollector includeCollector) throws ZserioEmitException
        {
            final Parameter parameter = instantiatedParameter.getParameter();
            final CppNativeType parameterNativeType =
                    cppNativeMapper.getCppType(parameter.getTypeReference());
            includeCollector.addHeaderIncludesForType(parameterNativeType);

            tableName = tableType.getName();
            fieldName = field.getName();
            definitionName = parameter.getName();
            cppTypeName = parameterNativeType.getFullName();
            isSimpleType = parameterNativeType.isSimpleType();
        }

        public boolean getIsExplicit()
        {
            return false;
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

        @Override
        public int compareTo(ParameterTemplateData other)
        {
            int result = tableName.compareTo(other.tableName);
            if (result != 0)
                return result;

            result = fieldName.compareTo(other.fieldName);
            if (result != 0)
                return result;

            result = definitionName.compareTo(other.definitionName);
            if (result != 0)
                return result;

            return cppTypeName.compareTo(other.cppTypeName);
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof ParameterTemplateData)
            {
                return compareTo((ParameterTemplateData)other) == 0;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, tableName);
            hash = HashUtil.hash(hash, fieldName);
            hash = HashUtil.hash(hash, definitionName);
            hash = HashUtil.hash(hash, cppTypeName);
            return hash;
        }

        private final String tableName;
        private final String fieldName;
        private final String definitionName;
        private final String cppTypeName;
        private final boolean isSimpleType;
    }

    public static class ExplicitParameterTemplateData implements Comparable<ExplicitParameterTemplateData>
    {
        public ExplicitParameterTemplateData(CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppSqlIndirectExpressionFormatter, SqlTableType tableType, Field field,
                InstantiatedParameter instantiatedParameter,
                IncludeCollector includeCollector) throws ZserioEmitException
        {
            final Parameter parameter = instantiatedParameter.getParameter();
            final CppNativeType parameterNativeType =
                    cppNativeMapper.getCppType(parameter.getTypeReference());
            includeCollector.addHeaderIncludesForType(parameterNativeType);
            final Expression argumentExpression = instantiatedParameter.getArgumentExpression();

            tableName = tableType.getName();
            expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
            cppTypeName = parameterNativeType.getFullName();
            isSimpleType = parameterNativeType.isSimpleType();
        }

        public boolean getIsExplicit()
        {
            return true;
        }

        public String getTableName()
        {
            return tableName;
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
            int result = tableName.compareTo(other.tableName);
            if (result != 0)
                return result;

            result = expression.compareTo(other.expression);
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
            hash = HashUtil.hash(hash, tableName);
            hash = HashUtil.hash(hash, expression);
            hash = HashUtil.hash(hash, cppTypeName);
            return hash;
        }

        private final String tableName;
        private final String expression;
        private final String cppTypeName;
        private final boolean isSimpleType;
    }

    private final TreeSet<ExplicitParameterTemplateData> explicitParameters =
            new TreeSet<ExplicitParameterTemplateData>();
    private final TreeSet<ParameterTemplateData> parameters = new TreeSet<ParameterTemplateData>();
}
