package zserio.emit.java;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public final class ParameterProviderTemplateData extends JavaTemplateData
{
    public ParameterProviderTemplateData(TemplateDataContext context, List<SqlTableType> sqlTableTypes)
            throws ZserioEmitException
    {
        super(context);

        rootPackageName = context.getJavaRootPackageName();
        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        this.javaSqlIndirectExpressionFormatter = context.getJavaSqlIndirectExpressionFormatter();
        sqlTableParameters = new TreeSet<SqlTableParameterTemplateData>();
        for (SqlTableType sqlTableType : sqlTableTypes)
        {
            for (Field field : sqlTableType.getFields())
            {
                final List<TypeInstantiation.InstantiatedParameter> parameters =
                        field.getInstantiatedParameters();
                for (TypeInstantiation.InstantiatedParameter parameter : parameters)
                {
                    sqlTableParameters.add(new SqlTableParameterTemplateData(javaNativeTypeMapper,
                            javaSqlIndirectExpressionFormatter, sqlTableType, parameter));
                }
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
    private final ExpressionFormatter                       javaSqlIndirectExpressionFormatter;
    private final SortedSet<SqlTableParameterTemplateData>  sqlTableParameters;
}
