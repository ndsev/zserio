package zserio.emit.cpp;

import java.util.List;
import java.util.TreeSet;

import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;

public class ParameterProviderTemplateData extends CppTemplateData
{
    public ParameterProviderTemplateData(TemplateDataContext context)
    {
        super(context);
        cppNativeTypeMapper = context.getCppNativeTypeMapper();
        cppExpressionFormatter = context.getExpressionFormatter(this);
        cppSqlIndirectExpressionFormatter = context.getSqlIndirectExpressionFormatter(this);
        sqlTableParameters = new TreeSet<SqlTableParameterTemplateData>();
    }

    public void add(SqlTableType tableType)
    {
        for (Field field : tableType.getFields())
        {
            final List<TypeInstantiation.InstantiatedParameter> parameters = field.getInstantiatedParameters();
            for (TypeInstantiation.InstantiatedParameter parameter : parameters)
            {
                sqlTableParameters.add(new SqlTableParameterTemplateData(cppNativeTypeMapper,
                        cppExpressionFormatter, cppSqlIndirectExpressionFormatter, tableType, field, parameter,
                        this));
            }
        }
    }

    public Iterable<SqlTableParameterTemplateData> getSqlTableParameters()
    {
        return sqlTableParameters;
    }

    private final CppNativeTypeMapper cppNativeTypeMapper;
    private final ExpressionFormatter cppExpressionFormatter;
    private final ExpressionFormatter cppSqlIndirectExpressionFormatter;
    private final TreeSet<SqlTableParameterTemplateData> sqlTableParameters;
}
