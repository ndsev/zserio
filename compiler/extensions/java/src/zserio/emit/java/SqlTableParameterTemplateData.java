package zserio.emit.java;

import zserio.ast.Expression;
import zserio.ast.Parameter;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.tools.HashUtil;

public class SqlTableParameterTemplateData implements Comparable<SqlTableParameterTemplateData>
{
    public SqlTableParameterTemplateData(JavaNativeTypeMapper javaNativeTypeMapper,
            ExpressionFormatter javaSqlIndirectExpressionFormatter, SqlTableType tableType,
            TypeInstantiation.InstantiatedParameter instantiatedParameter)
    {
        tableName = tableType.getName();
        final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
        isExplicit = argumentExpression.isExplicitVariable();
        expression = javaSqlIndirectExpressionFormatter.formatGetter(argumentExpression);
        final Parameter parameter = instantiatedParameter.getParameter();
        javaTypeName = javaNativeTypeMapper.getJavaType(parameter.getParameterType()).getFullName();
    }

    public String getTableName()
    {
        return tableName;
    }

    public boolean getIsExplicit()
    {
        return isExplicit;
    }

    public String getExpression()
    {
        return expression;
    }

    public String getJavaTypeName()
    {
        return javaTypeName;
    }

    @Override
    public int compareTo(SqlTableParameterTemplateData other)
    {
        int result = tableName.compareTo(other.tableName);
        if (result != 0)
            return result;

        result = expression.compareTo(other.expression);
        if (result != 0)
            return result;

        return javaTypeName.compareTo(other.javaTypeName);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof SqlTableParameterTemplateData)
        {
            return compareTo((SqlTableParameterTemplateData)other) == 0;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, tableName);
        hash = HashUtil.hash(hash, expression);
        hash = HashUtil.hash(hash, javaTypeName);
        return hash;
    }

    private final String    tableName;
    private final boolean   isExplicit;
    private final String    expression;
    private final String    javaTypeName;
}
