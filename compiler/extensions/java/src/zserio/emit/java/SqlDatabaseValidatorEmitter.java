package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

final class SqlDatabaseValidatorEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseValidatorEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
        generateValidatableSqlDatabase = false;
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode() && getWithValidationCode())
            generateValidatableSqlDatabase = true;
    }

    @Override
    public void endRoot() throws ZserioEmitJavaException
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

    private boolean generateValidatableSqlDatabase;
}
