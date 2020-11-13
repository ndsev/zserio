package zserio.extension.python;

import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class SqlTableEmitter extends PythonDefaultEmitter
{
    public SqlTableEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        if (getWithSqlCode())
        {
            final Object templateData = new SqlTableEmitterTemplateData(getTemplateDataContext(), sqlTableType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlTableType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlTable.py.ftl";
}
