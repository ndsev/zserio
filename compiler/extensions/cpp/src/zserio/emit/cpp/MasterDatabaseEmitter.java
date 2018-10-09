package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

public class MasterDatabaseEmitter extends CppDefaultEmitter
{
    public MasterDatabaseEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitCppException
    {
        if (getWithSqlCode())
            sqlDatabaseTypes.add(sqlDatabaseType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitCppException
    {
        if (!sqlDatabaseTypes.isEmpty())
        {
            final MasterDatabaseTemplateData templateData =
                    new MasterDatabaseTemplateData(getTemplateDataContext(), sqlDatabaseTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(TEMPLATE_SOURCE_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "MasterDatabase.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "MasterDatabase.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "MasterDatabase";

    private final List<SqlDatabaseType> sqlDatabaseTypes = new ArrayList<SqlDatabaseType>();
}
