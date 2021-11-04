package zserio.ast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class implements checking of rule IDs to be unique.
 *
 * Rule ID must be unique using case insensitive comparison.
 */
class RuleIdUniqueChecker extends ZserioAstWalker
{
    /**
     * Constructor to check uniqueness within a single package.
     */
    public RuleIdUniqueChecker()
    {
        this(false);
    }

    /**
     * Constructor from withGlobalRuleIdCheck.
     *
     * @param withGlobalRuleIdCheck Whether to check of rule id uniqueness between all packages.
     */
    public RuleIdUniqueChecker(boolean withGlobalRuleIdCheck)
    {
        this.withGlobalRuleIdCheck = withGlobalRuleIdCheck;
    }

    @Override
    public void visitRule(Rule rule)
    {
        final String lowerCaseRuleId = rule.getRuleId().toLowerCase(Locale.ENGLISH);
        final Rule addedRule = rulesMap.put(lowerCaseRuleId, rule);
        if (addedRule != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(rule.getLocation(),
                    "Rule ID '" + rule.getRuleId() + "' is not unique (case insensitive) " +
                            ((withGlobalRuleIdCheck) ? "between all packages" : "within a package") + "!");
            stackedException.pushMessage(addedRule.getLocation(), "    Conflicting rule defined here.");
            throw stackedException;
        }
    }

    private final boolean withGlobalRuleIdCheck;
    private final Map<String, Rule> rulesMap = new HashMap<String, Rule>();
}
