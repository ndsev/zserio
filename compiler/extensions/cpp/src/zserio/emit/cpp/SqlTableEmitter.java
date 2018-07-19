package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

public class SqlTableEmitter extends CppDefaultEmitter
{
    public SqlTableEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSqlTable(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode())
        {
            final SqlTableType tableType = (SqlTableType)token;
            final String tableRowName = tableType.getName() + TABLE_ROW_SUFFIX_NAME;
            final Object tableTemplateData = new SqlTableEmitterTemplateData(getTemplateDataContext(),
                    tableType, tableRowName);
            processHeaderTemplate(TABLE_TEMPLATE_HEADER_NAME, tableTemplateData, tableType);
            processSourceTemplate(TABLE_TEMPLATE_SOURCE_NAME, tableTemplateData, tableType);

            final Object tableRowTemplateData = new SqlTableRowEmitterTemplateData(getTemplateDataContext(),
                    tableType, tableRowName);
            processHeaderTemplate(TABLE_ROW_TEMPLATE_HEADER_NAME, tableRowTemplateData, tableType,
                    tableRowName);
            processSourceTemplate(TABLE_ROW_TEMPLATE_SOURCE_NAME, tableRowTemplateData, tableType,
                    tableRowName);
        }
    }

    private static final String TABLE_TEMPLATE_SOURCE_NAME = "SqlTable.cpp.ftl";
    private static final String TABLE_TEMPLATE_HEADER_NAME = "SqlTable.h.ftl";
    private static final String TABLE_ROW_TEMPLATE_SOURCE_NAME = "SqlTableRow.cpp.ftl";
    private static final String TABLE_ROW_TEMPLATE_HEADER_NAME = "SqlTableRow.h.ftl";

    private static final String TABLE_ROW_SUFFIX_NAME = "Row";
}
