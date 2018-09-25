package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

class MasterDatabaseEmitter extends JavaDefaultEmitter
{
    public MasterDatabaseEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
            sqlDatabaseTypes.add((SqlDatabaseType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitJavaException
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
