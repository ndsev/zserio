package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class MasterDatabaseEmitter extends JavaDefaultEmitter
{
    public MasterDatabaseEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        if (getWithSqlCode())
            sqlDatabaseTypes.add(sqlDatabaseType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (!sqlDatabaseTypes.isEmpty())
        {
            templateData = new MasterDatabaseTemplateData(getTemplateDataContext(), sqlDatabaseTypes);
            processTemplateToRootDir(TEMPLATE_NAME, templateData, OUTPUT_FILE_NAME);
        }
    }

    private static final String TEMPLATE_NAME = "MasterDatabase.java.ftl";
    private static final String OUTPUT_FILE_NAME = "MasterDatabase";

    private final List<SqlDatabaseType> sqlDatabaseTypes = new ArrayList<SqlDatabaseType>();
    private MasterDatabaseTemplateData templateData;
}
