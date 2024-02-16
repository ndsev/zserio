package zserio.extension.cpp;

/**
 * A special formatting policy to be used in generated helper classes (e.g. factories) which need indirect
 * references.
 */
public final class CppIndirectExpressionFormattingPolicy extends CppExpressionFormattingPolicy
{
    public CppIndirectExpressionFormattingPolicy(
            TemplateDataContext context, IncludeCollector includeCollector, String compoundTypeAccessPrefix)
    {
        super(context, includeCollector);
        this.compoundTypeAccessPrefix = compoundTypeAccessPrefix;
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return compoundTypeAccessPrefix;
    }

    private final String compoundTypeAccessPrefix;
}
