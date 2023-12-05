package zserio.extension.java;

import zserio.ast.SqlTableType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * SQL table emitter.
 */
final class SqlTableEmitter extends JavaDefaultEmitter
{
    public SqlTableEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
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

    static final String TABLE_ROW_SUFFIX_NAME = "Row";
    private static final String TABLE_TEMPLATE_NAME = "SqlTable.java.ftl";
    private static final String TABLE_ROW_TEMPLATE_NAME = "SqlTableRow.java.ftl";
}
