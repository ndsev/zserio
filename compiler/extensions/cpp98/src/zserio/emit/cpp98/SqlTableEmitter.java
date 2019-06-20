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
            final String tableRowName = sqlTableType.getName() + TABLE_ROW_SUFFIX_NAME;
            final Object tableTemplateData = new SqlTableEmitterTemplateData(getTemplateDataContext(),
                    sqlTableType, tableRowName);
            processHeaderTemplate(TABLE_TEMPLATE_HEADER_NAME, tableTemplateData, sqlTableType);
            processSourceTemplate(TABLE_TEMPLATE_SOURCE_NAME, tableTemplateData, sqlTableType);

            final Object tableRowTemplateData = new SqlTableRowEmitterTemplateData(getTemplateDataContext(),
                    sqlTableType, tableRowName);
            processHeaderTemplate(TABLE_ROW_TEMPLATE_HEADER_NAME, tableRowTemplateData, sqlTableType,
                    tableRowName);
            processSourceTemplate(TABLE_ROW_TEMPLATE_SOURCE_NAME, tableRowTemplateData, sqlTableType,
                    tableRowName);
        }
    }

    private static final String TABLE_TEMPLATE_SOURCE_NAME = "SqlTable.cpp.ftl";
    private static final String TABLE_TEMPLATE_HEADER_NAME = "SqlTable.h.ftl";
    private static final String TABLE_ROW_TEMPLATE_SOURCE_NAME = "SqlTableRow.cpp.ftl";
    private static final String TABLE_ROW_TEMPLATE_HEADER_NAME = "SqlTableRow.h.ftl";

    private static final String TABLE_ROW_SUFFIX_NAME = "Row";
}
