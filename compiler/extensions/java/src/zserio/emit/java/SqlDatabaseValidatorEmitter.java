package zserio.emit.java;

import zserio.ast.Root;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

final class SqlDatabaseValidatorEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseValidatorEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithSqlCode() && getWithValidationCode())
            generateValidatableSqlDatabase = true;
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (generateValidatableSqlDatabase)
        {
            final SqlDatabaseValidatorEmitterTemplateData templateData =
                    new SqlDatabaseValidatorEmitterTemplateData(getTemplateDataContext());

            processTemplateToRootDir(TEMPLATE_NAME, templateData, OUTPUT_FILE_NAME);
        }
    }

    private static final String TEMPLATE_NAME = "SqlDatabaseValidator.java.ftl";
    private static final String OUTPUT_FILE_NAME = "SqlDatabaseValidator";

    private boolean generateValidatableSqlDatabase = false;
}
