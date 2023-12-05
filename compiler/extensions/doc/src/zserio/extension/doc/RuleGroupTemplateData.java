package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rule;
import zserio.ast.RuleGroup;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for rule groups in the package used by Package emitter.
 */
public final class RuleGroupTemplateData extends ContentTemplateDataBase
{
    public RuleGroupTemplateData(ContentTemplateDataContext context, RuleGroup ruleGroup)
            throws ZserioExtensionException
    {
        super(context, ruleGroup);

        for (Rule rule : ruleGroup.getRules())
            rules.add(new RuleTemplateData(context, ruleGroup, rule));
    }

    public Iterable<RuleTemplateData> getRules()
    {
        return rules;
    }

    public static final class RuleTemplateData
    {
        RuleTemplateData(ContentTemplateDataContext context, RuleGroup ruleGroup, Rule rule)
        {
            symbol = SymbolTemplateDataCreator.createData(context, ruleGroup, rule);
            docComments = new DocCommentsTemplateData(context, rule.getDocComments());
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final SymbolTemplateData symbol;
        private final DocCommentsTemplateData docComments;
    }

    private final List<RuleTemplateData> rules = new ArrayList<RuleTemplateData>();
}
