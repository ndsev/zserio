package zserio.emit.cpp;

import zserio.ast.ZserioTypeUtil;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Parameter;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeUserType;
import zserio.emit.cpp.types.NativeEnumType;
import zserio.tools.HashUtil;

public class SqlTableParameterTemplateData implements Comparable<SqlTableParameterTemplateData>
{
    public SqlTableParameterTemplateData(CppNativeTypeMapper cppNativeTypeMapper,
            ExpressionFormatter cppExpressionFormatter, ExpressionFormatter cppSqlIndirectExpressionFormatter,
            SqlTableType tableType, Field field, TypeInstantiation.InstantiatedParameter instantiatedParameter,
            IncludeCollector includeCollector)
    {
        final Parameter parameter = instantiatedParameter.getParameter();
        final CppNativeType parameterNativeType = cppNativeTypeMapper.getCppType(parameter.getParameterType());
        includeCollector.addHeaderIncludesForType(parameterNativeType);

        final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
        isExplicit = argumentExpression.isExplicitVariable();
        tableName = tableType.getName();
        fieldName = field.getName();
        definitionName = parameter.getName();
        zserioTypeName = ZserioTypeUtil.getFullName(tableType);
        cppTypeName = parameterNativeType.getFullName();
        expression = cppSqlIndirectExpressionFormatter.formatGetter(argumentExpression);

        isCompoundType = parameterNativeType instanceof NativeUserType;
        isEnumType = parameterNativeType instanceof NativeEnumType;
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

    public String getZserioTypeName()
    {
        return zserioTypeName;
    }

    public String getCppTypeName()
    {
        return cppTypeName;
    }

    public boolean getIsExplicit()
    {
        return isExplicit;
    }

    public String getExpression()
    {
        return expression;
    }

    public boolean getIsCompoundType()
    {
        return isCompoundType;
    }

    public boolean getIsEnumType()
    {
        return isEnumType;
    }

    @Override
    public int compareTo(SqlTableParameterTemplateData other)
    {
        int result = tableName.compareTo(other.tableName);
        if (result != 0)
            return result;

        if (isExplicit)
        {
            result = expression.compareTo(other.expression);
            if (result != 0)
                return result;
        }
        else
        {
            result = fieldName.compareTo(other.fieldName);
            if (result != 0)
                return result;

            result = definitionName.compareTo(other.definitionName);
            if (result != 0)
                return result;
        }

        return cppTypeName.compareTo(other.cppTypeName);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof SqlTableParameterTemplateData)
            return compareTo((SqlTableParameterTemplateData)other) == 0;

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, tableName);
        if (isExplicit)
        {
            hash = HashUtil.hash(hash, expression);
        }
        else
        {
            hash = HashUtil.hash(hash, fieldName);
            hash = HashUtil.hash(hash, definitionName);
        }
        hash = HashUtil.hash(hash, cppTypeName);

        return hash;
    }

    private final String tableName;
    private final String fieldName;
    private final String definitionName;
    private final String zserioTypeName;
    private final String cppTypeName;
    private final boolean isExplicit;
    private final String expression;
    private final boolean isCompoundType;
    private final boolean isEnumType;
}
