package zserio.emit.cpp98;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public class CppSqlIndirectExpressionFormattingPolicy extends CppDefaultExpressionFormattingPolicy
{
    public CppSqlIndirectExpressionFormattingPolicy(CppNativeMapper cppNativeMapper,
            IncludeCollector includeCollector)
    {
        super(cppNativeMapper, includeCollector);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return SQL_INDIRECT_PREFIX;
    }

    private static final String SQL_INDIRECT_PREFIX = "row";
}
