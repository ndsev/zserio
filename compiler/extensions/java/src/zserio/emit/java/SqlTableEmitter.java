package zserio.emit.java;

import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class SqlTableEmitter extends JavaDefaultEmitter
{
    public SqlTableEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithSqlCode())
        {
            final String tableRowName = sqlTableType.getName() + TABLE_ROW_SUFFIX_NAME;
            final TemplateDataContext context = getTemplateDataContext();
            final Object tableTemplateData = new SqlTableEmitterTemplateData(context, sqlTableType, tableRowName);
            processTemplate(TABLE_TEMPLATE_NAME, tableTemplateData, sqlTableType);

            final Object tableRowTemplateData = new SqlTableRowEmitterTemplateData(context, sqlTableType,
                    tableRowName);
            processTemplate(TABLE_ROW_TEMPLATE_NAME, tableRowTemplateData, sqlTableType, tableRowName);
        }
    }

    private static final String TABLE_TEMPLATE_NAME = "SqlTable.java.ftl";
    private static final String TABLE_ROW_TEMPLATE_NAME = "SqlTableRow.java.ftl";
    private static final String TABLE_ROW_SUFFIX_NAME = "Row";
}
