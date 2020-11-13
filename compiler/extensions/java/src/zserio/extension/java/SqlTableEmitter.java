package zserio.extension.java;

import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class SqlTableEmitter extends JavaDefaultEmitter
{
    public SqlTableEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
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
