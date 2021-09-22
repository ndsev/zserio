package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class implements checking of rule IDs to be unique between all packages.
 *
 * Rule ID must be unique globally using case insensitive comparison.
 */
class RuleIdUniqueChecker extends ZserioAstWalker
{
    @Override
    public void visitRule(Rule rule)
    {
        final String lowerCaseRuleId = rule.getRuleId().toLowerCase(Locale.ENGLISH);
        final Rule addedRule = rulesMap.put(lowerCaseRuleId, rule);
        if (addedRule != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(rule.getLocation(),
                    "Rule ID '" + rule.getRuleId() + "' is not unique (case insensitive)!");
            stackedException.pushMessage(addedRule.getLocation(), "    Conflicting rule defined here.");
            throw stackedException;
        }
    }

    private Map<String, Rule> rulesMap = new HashMap<String, Rule>();
}
