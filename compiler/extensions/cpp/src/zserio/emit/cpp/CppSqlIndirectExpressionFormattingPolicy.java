package zserio.emit.cpp;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public class CppSqlIndirectExpressionFormattingPolicy extends CppDefaultExpressionFormattingPolicy
{
    public CppSqlIndirectExpressionFormattingPolicy(CppNativeTypeMapper cppNativeTypeMapper,
            IncludeCollector includeCollector)
    {
        super(cppNativeTypeMapper, includeCollector);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return SQL_INDIRECT_PREFIX;
    }

    private static final String SQL_INDIRECT_PREFIX = "row";
}
