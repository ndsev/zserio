package zserio.emit.java;

public final class SqlDatabaseValidatorEmitterTemplateData extends JavaTemplateData
{
    public SqlDatabaseValidatorEmitterTemplateData(TemplateDataContext context)
    {
        super(context);
        rootPackageName = context.getJavaRootPackageName();
    }

    public String getRootPackageName()
    {
        return rootPackageName;
    }

    private final String rootPackageName;
}
