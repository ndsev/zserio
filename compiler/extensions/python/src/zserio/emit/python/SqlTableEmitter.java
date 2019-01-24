package zserio.emit.python;

import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class SqlTableEmitter extends PythonDefaultEmitter
{
    public SqlTableEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithSqlCode())
        {
            final Object templateData = new SqlTableEmitterTemplateData(getTemplateDataContext(), sqlTableType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlTableType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlTable.py.ftl";
}
