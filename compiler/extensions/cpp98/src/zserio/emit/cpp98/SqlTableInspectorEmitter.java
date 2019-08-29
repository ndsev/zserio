package zserio.emit.cpp98;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class SqlTableInspectorEmitter extends CppDefaultEmitter
{
    public SqlTableInspectorEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (getWithSqlCode() && getWithInspectorCode())
        {
            final Object templateData = new SqlTableInspectorTemplateData(getTemplateDataContext());

            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "ISqlTableInspector.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "ISqlTableInspector";
}
