package zserio.extension.cpp;

import zserio.ast.SqlTableType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * SQL table emitter.
 */
public final class SqlTableEmitter extends CppDefaultEmitter
{
    public SqlTableEmitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters)
    {
        super(outputFileManager, cppParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
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
