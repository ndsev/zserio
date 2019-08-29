package zserio.emit.cpp;

import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class SqlTableEmitter extends CppDefaultEmitter
{
    public SqlTableEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithSqlCode())
        {
            final Object tableTemplateData = new SqlTableEmitterTemplateData(getTemplateDataContext(),
                    sqlTableType);
            processHeaderTemplate(TABLE_TEMPLATE_HEADER_NAME, tableTemplateData, sqlTableType);
            processSourceTemplate(TABLE_TEMPLATE_SOURCE_NAME, tableTemplateData, sqlTableType);
        }
    }

    private static final String TABLE_TEMPLATE_SOURCE_NAME = "SqlTable.cpp.ftl";
    private static final String TABLE_TEMPLATE_HEADER_NAME = "SqlTable.h.ftl";
}
