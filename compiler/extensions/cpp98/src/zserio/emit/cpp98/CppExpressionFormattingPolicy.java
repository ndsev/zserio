package zserio.emit.cpp98;

/**
 * Formatting policy for C++ expressions.
 *
 * This policy does work for everything except for external classes which need indirect policy.
 */
public class CppExpressionFormattingPolicy extends CppDefaultExpressionFormattingPolicy
{
    public CppExpressionFormattingPolicy(CppNativeMapper cppNativeMapper,
            IncludeCollector includeCollector)
    {
        super(cppNativeMapper, includeCollector);
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return "";
    }
}
