package zserio.emit.cpp;

import java.util.List;
import java.util.TreeSet;

import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;

public class ParameterProviderTemplateData extends CppTemplateData
{
    public ParameterProviderTemplateData(TemplateDataContext context, List<SqlTableType> sqlTableTypes)
    {
        super(context);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        final ExpressionFormatter cppSqlIndirectExpressionFormatter =
                context.getSqlIndirectExpressionFormatter(this);
        sqlTableParameters = new TreeSet<SqlTableParameterTemplateData>();

        for (SqlTableType sqlTableType : sqlTableTypes)
        {
            for (Field field : sqlTableType.getFields())
            {
                final List<TypeInstantiation.InstantiatedParameter> parameters =
                        field.getInstantiatedParameters();
                for (TypeInstantiation.InstantiatedParameter parameter : parameters)
                {
                    sqlTableParameters.add(new SqlTableParameterTemplateData(cppNativeTypeMapper,
                            cppExpressionFormatter, cppSqlIndirectExpressionFormatter, sqlTableType, field,
                            parameter, this));
                }
            }
        }
    }

    public Iterable<SqlTableParameterTemplateData> getSqlTableParameters()
    {
        return sqlTableParameters;
    }

    private final TreeSet<SqlTableParameterTemplateData> sqlTableParameters;
}
