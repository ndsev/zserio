package zserio.emit.java;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;

public final class ParameterProviderTemplateData extends JavaTemplateData
{
    public ParameterProviderTemplateData(TemplateDataContext context)
    {
        super(context);

        rootPackageName = context.getJavaRootPackageName();
        this.javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        this.javaSqlIndirectExpressionFormatter = context.getJavaSqlIndirectExpressionFormatter();
        sqlTableParameters = new TreeSet<SqlTableParameterTemplateData>();
    }

    public void add(SqlTableType tableType)
    {
        for (Field field : tableType.getFields())
        {
            final List<TypeInstantiation.InstantiatedParameter> parameters = field.getInstantiatedParameters();
            for (TypeInstantiation.InstantiatedParameter parameter : parameters)
            {
                sqlTableParameters.add(new SqlTableParameterTemplateData(javaNativeTypeMapper,
                        javaSqlIndirectExpressionFormatter, tableType, parameter));
            }
        }
    }

    public String getRootPackageName()
    {
        return rootPackageName;
    }

    public Iterable<SqlTableParameterTemplateData> getSqlTableParameters()
    {
        return sqlTableParameters;
    }

    private final String                                    rootPackageName;
    private final JavaNativeTypeMapper                      javaNativeTypeMapper;
    private final ExpressionFormatter                       javaSqlIndirectExpressionFormatter;
    private final SortedSet<SqlTableParameterTemplateData>  sqlTableParameters;
}
