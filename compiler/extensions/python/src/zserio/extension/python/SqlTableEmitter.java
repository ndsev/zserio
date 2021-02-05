package zserio.extension.python;

import zserio.ast.SqlTableType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

public class SqlTableEmitter extends CompoundEmitter
{
    public SqlTableEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
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
