package zserio.extension.python;

import zserio.ast.SqlTableType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * SQL table emitter.
 */
final class SqlTableEmitter extends PythonDefaultEmitter
{
    public SqlTableEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, pythonParameters, packedTypesCollector);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        if (!getWithSqlCode())
            return;

        final SqlTableEmitterTemplateData templateData =
                new SqlTableEmitterTemplateData(getTemplateDataContext(), sqlTableType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlTableType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlTable.py.ftl";
}
