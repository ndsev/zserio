package zserio.ast;

import java.util.regex.Pattern;

/**
 * Checks that rule identifier is valid.
 */
class RuleIdValidator
{
    public static void validate(String ruleId, AstLocation ruleIdLocation)
    {
        if (!Pattern.matches(RULE_ID_PATTERN, ruleId))
        {
            throw new ParserException(ruleIdLocation,
                    "Invalid rule identifier! Rule identifier must match pattern '" + RULE_ID_PATTERN + "'!");
        }
    }

    private static String RULE_ID_PATTERN = "[a-zA-Z][a-zA-Z0-9_:.-]*";
}
