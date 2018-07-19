package zserio.emit.java;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public class JavaSqlIndirectExpressionFormattingPolicy extends JavaExpressionFormattingPolicy
{
    public JavaSqlIndirectExpressionFormattingPolicy(JavaNativeTypeMapper javaNativeTypeMapper)
    {
        super(javaNativeTypeMapper);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return "row";
    }
}
