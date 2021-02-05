package zserio.extension.cpp;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

public class SqlDatabaseEmitter extends CppDefaultEmitter
{
    public SqlDatabaseEmitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters)
    {
        super(outputFileManager, cppParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (getWithSqlCode())
        {
            final Object templateData = new SqlDatabaseEmitterTemplateData(getTemplateDataContext(),
                    sqlDatabaseType);

            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, sqlDatabaseType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlDatabaseType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlDatabase.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "SqlDatabase.h.ftl";
}
