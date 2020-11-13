package zserio.extension.java;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public class JavaSqlIndirectExpressionFormattingPolicy extends JavaExpressionFormattingPolicy
{
    public JavaSqlIndirectExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return "row";
    }
}
