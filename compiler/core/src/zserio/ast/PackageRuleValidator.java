package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class implements validation of rule IDs within a single package.
 *
 * Rule ID must be unique using case insensitive comparison within a single package.
 */
class PackageRuleValidator extends ZserioAstWalker
{
    @Override
    public void visitRule(Rule rule)
    {
        final String lowerCaseRuleId = rule.getRuleId().toLowerCase(Locale.ENGLISH);
        final Rule addedRule = rulesMap.put(lowerCaseRuleId, rule);
        if (addedRule != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(rule.getLocation(),
                    "Rule ID '" + rule.getRuleId() + "' is not unique (case insensitive) within this package!");
            stackedException.pushMessage(addedRule.getLocation(), "    Conflicting rule defined here.");
            throw stackedException;
        }
    }

    private Map<String, Rule> rulesMap = new HashMap<String, Rule>();
}
