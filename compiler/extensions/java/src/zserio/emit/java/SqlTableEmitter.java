package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

class SqlTableEmitter extends JavaDefaultEmitter
{
    public SqlTableEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode())
        {
            final SqlTableType tableType = (SqlTableType)token;
            final String tableRowName = tableType.getName() + TABLE_ROW_SUFFIX_NAME;
            final TemplateDataContext context = getTemplateDataContext();
            final Object tableTemplateData = new SqlTableEmitterTemplateData(context, tableType, tableRowName);
            processTemplate(TABLE_TEMPLATE_NAME, tableTemplateData, tableType);

            final Object tableRowTemplateData = new SqlTableRowEmitterTemplateData(context, tableType,
                    tableRowName);
            processTemplate(TABLE_ROW_TEMPLATE_NAME, tableRowTemplateData, tableType, tableRowName);
        }
    }

    private static final String TABLE_TEMPLATE_NAME = "SqlTable.java.ftl";
    private static final String TABLE_ROW_TEMPLATE_NAME = "SqlTableRow.java.ftl";
    private static final String TABLE_ROW_SUFFIX_NAME = "Row";
}
