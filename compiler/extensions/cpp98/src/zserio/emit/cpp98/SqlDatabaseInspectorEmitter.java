package zserio.emit.cpp;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class SqlDatabaseInspectorEmitter extends CppDefaultEmitter
{
    public SqlDatabaseInspectorEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (getWithSqlCode() && getWithInspectorCode())
        {
            final Object templateData = new SqlDatabaseInspectorTemplateData(getTemplateDataContext());

            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "ISqlDatabaseInspector.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "ISqlDatabaseInspector";
}
