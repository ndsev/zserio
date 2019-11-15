package zserio.emit.cpp;

/**
 * A special formatting policy to be used in generated helper classes (e.g. factories) which need indirect
 * references.
 */
public class CppOwnerIndirectExpressionFormattingPolicy extends CppExpressionFormattingPolicy
{
    public CppOwnerIndirectExpressionFormattingPolicy(CppNativeMapper cppNativeMapper,
            IncludeCollector includeCollector)
    {
        super(cppNativeMapper, includeCollector);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return OWNER_INDIRECT_PREFIX;
    }

    private static final String OWNER_INDIRECT_PREFIX = "m_owner";
}
